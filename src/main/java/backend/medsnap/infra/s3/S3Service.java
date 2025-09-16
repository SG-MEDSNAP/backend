package backend.medsnap.infra.s3;

import java.time.Instant;
import java.util.UUID;

import backend.medsnap.infra.s3.exception.S3DeleteFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import backend.medsnap.infra.s3.exception.S3UploadFailException;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /** MultipartFile을 S3에 업로드하고 URL을 반환 */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String ext = getExtension(file.getOriginalFilename());
            String key = buildKey(folder, ext);

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(
                                    file.getContentType() != null
                                            ? file.getContentType()
                                            : "application/octet-stream")
                            .build();

            try (var inputStream = file.getInputStream()) {
                s3Client.putObject(
                        putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            }

            return s3Client.utilities()
                    .getUrl(GetUrlRequest.builder().bucket(bucketName).key(key).build())
                    .toString();

        } catch (Exception e) {
            log.error("S3 파일 업로드 실패 - 파일: {}", file.getOriginalFilename(), e);
            throw new S3UploadFailException();
        }
    }

    /**
     * S3에서 파일 삭제 (URL 기반)
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("삭제할 파일 URL이 비어있습니다.");
            return;
        }

        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key == null) {
                log.warn("URL에서 키 추출 실패: {}", fileUrl);
                return;
            }
            deleteFileByKey(key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패 - URL: {}", fileUrl, e);
            throw new S3DeleteFailException("파일 삭제 실패", e);
        }
    }

    /** S3에서 파일 삭제 (Key 기반) */
    public void deleteFileByKey(String key) {
        if (key == null || key.isEmpty()) {
            log.warn("삭제할 파일 키가 비어있습니다.");
            return;
        }

        try {
            DeleteObjectRequest deleteObjectRequest =
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공 - Key: {}", key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패 - Key: {}", key, e);
            throw new S3DeleteFailException("파일 삭제 실패", e);
        }
    }

    /** URL에서 S3 Key 추출 */
    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        try {
            // URL을 '/'로 분할하여 도메인 부분을 제외한 path 추출
            String[] parts = fileUrl.split("/");
            if (parts.length < 4) {
                log.warn("잘못된 URL 형식: {}", fileUrl);
                return null;
            }

            // https://domain/key 형태에서 key 부분만 추출
            StringBuilder keyBuilder = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (i > 3) keyBuilder.append("/");
                keyBuilder.append(parts[i]);
            }

            String key = keyBuilder.toString();
            return key.isEmpty() ? null : key;
        } catch (Exception e) {
            log.warn("URL 파싱 중 오류 발생: {}", fileUrl, e);
            return null;
        }

    }

    /** 파일 확장자 추출 */
    public String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg"; // 기본값
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /** 업로드 키 생성 */
    private String buildKey(String folder, String ext) {
        String cleanFolder =
                (folder == null || folder.isBlank())
                        ? "uploads"
                        : folder.replaceAll("^/|/$", "").replaceAll("/{2,}", "/");
        return String.format(
                "%s/%s_%d.%s", cleanFolder, UUID.randomUUID(), Instant.now().toEpochMilli(), ext);
    }
}
