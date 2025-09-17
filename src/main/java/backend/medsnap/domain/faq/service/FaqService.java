package backend.medsnap.domain.faq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.faq.dto.request.FaqRequest;
import backend.medsnap.domain.faq.dto.response.FaqResponse;
import backend.medsnap.domain.faq.entity.Faq;
import backend.medsnap.domain.faq.exception.FaqNotFoundException;
import backend.medsnap.domain.faq.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    @Transactional
    public FaqResponse createFaq(FaqRequest request) {
        log.info("FAQ 등록 시작");

        Faq faq =
                Faq.builder()
                        .question(request.getQuestion())
                        .answer(request.getAnswer())
                        .category(request.getCategory())
                        .build();

        Faq savedFaq = faqRepository.save(faq);

        return FaqResponse.builder()
                .id(savedFaq.getId())
                .question(savedFaq.getQuestion())
                .answer(savedFaq.getAnswer())
                .category(savedFaq.getCategory())
                .createdAt(savedFaq.getCreatedAt())
                .updatedAt(savedFaq.getUpdatedAt())
                .build();
    }

    @Transactional
    public FaqResponse updateFaq(Long faqId, FaqRequest request) {
        log.info("FAQ 수정 시작 - ID: {}", faqId);

        // FAQ 존재 여부 확인
        Faq faq = faqRepository.findById(faqId).orElseThrow(() -> new FaqNotFoundException(faqId));

        faq.update(request.getQuestion(), request.getAnswer(), request.getCategory());

        return FaqResponse.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteFaq(Long faqId) {
        log.info("FAQ 삭제 시작 - ID: {}", faqId);

        // FAQ 존재 여부 확인
        Faq faq = faqRepository.findById(faqId).orElseThrow(() -> new FaqNotFoundException(faqId));

        faqRepository.delete(faq);
        log.info("FAQ ID: {}가 삭제되었습니다.", faqId);
    }
}
