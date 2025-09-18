package backend.medsnap.domain.faq.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.medsnap.domain.faq.dto.request.FaqRequest;
import backend.medsnap.domain.faq.dto.response.FaqResponse;
import backend.medsnap.domain.faq.service.FaqService;
import backend.medsnap.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<FaqResponse>>> getAllFaq() {
        List<FaqResponse> response = faqService.getAllFaq();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PutMapping("/{faqId}")
    public ResponseEntity<ApiResponse<FaqResponse>> updateFaq(
            @PathVariable Long faqId, @Valid @RequestBody FaqRequest request) {
        FaqResponse response = faqService.updateFaq(faqId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @DeleteMapping("/{faqId}")
    public ResponseEntity<Void> deleteFaq(@PathVariable("faqId") Long faqId) {
        faqService.deleteFaq(faqId);

        return ResponseEntity.noContent().build();
    }
}
