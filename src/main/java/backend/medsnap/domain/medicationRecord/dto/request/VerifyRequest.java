package backend.medsnap.domain.medicationRecord.dto.request;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyRequest {

    @NotNull private MultipartFile image;
}
