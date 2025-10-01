package backend.medsnap.domain.medicationRecord.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyRequest {

    @NotBlank
    private String imageUrl;
}
