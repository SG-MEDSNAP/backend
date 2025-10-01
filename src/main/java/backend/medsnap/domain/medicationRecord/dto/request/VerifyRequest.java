package backend.medsnap.domain.medicationRecord.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class VerifyRequest {

    @NotNull
    private MultipartFile image;
}
