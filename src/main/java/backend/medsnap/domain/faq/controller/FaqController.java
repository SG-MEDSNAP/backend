package backend.medsnap.domain.faq.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.medsnap.domain.faq.dto.request.FaqRequest;
import backend.medsnap.domain.faq.dto.response.FaqResponse;
import backend.medsnap.domain.faq.service.FaqService;
import backend.medsnap.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/faq")
@RequiredArgsConstructor
public class FaqController implements FaqSwagger {

    private final FaqService faqService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<FaqResponse>> createFaq(
            @Valid @RequestBody FaqRequest request) {
        FaqResponse response = faqService.createFaq(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }
}
