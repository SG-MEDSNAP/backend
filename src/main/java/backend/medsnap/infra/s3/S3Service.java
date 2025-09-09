package backend.medsnap.infra.s3;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import backend.medsnap.infra.s3.exception.S3UploadFailException;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return s3Client.utilities()
                    .getUrl(GetUrlRequest.builder().bucket(bucketName).key(key).build())
                    .toString();
        } catch (Exception e) {
            throw new S3UploadFailException();
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
                (folder == null || folder.isBlank()) ? "uploads" : folder.replace("^/|/$", "");
        return String.format(
                "%s/%s_%d.%s", cleanFolder, UUID.randomUUID(), Instant.now().toEpochMilli(), ext);
    }
}
