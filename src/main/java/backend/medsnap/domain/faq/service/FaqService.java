package backend.medsnap.domain.faq.service;

import java.util.List;
import java.util.stream.Collectors;

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

        return getFaqResponse(savedFaq);
    }

    @Transactional(readOnly = true)
    public List<FaqResponse> getAllFaq() {
        log.info("전체 FAQ 목록 조회 시작");

        return faqRepository.findAll().stream()
                .map(this::getFaqResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FaqResponse updateFaq(Long faqId, FaqRequest request) {
        log.info("FAQ 수정 시작 - ID: {}", faqId);

        // FAQ 존재 여부 확인
        Faq faq = faqRepository.findById(faqId).orElseThrow(() -> new FaqNotFoundException(faqId));

        faq.update(request.getQuestion(), request.getAnswer(), request.getCategory());

        return getFaqResponse(faq);
    }

    @Transactional
    public void deleteFaq(Long faqId) {
        log.info("FAQ 삭제 시작 - ID: {}", faqId);

        // FAQ 존재 여부 확인
        Faq faq = faqRepository.findById(faqId).orElseThrow(() -> new FaqNotFoundException(faqId));

        faq.softDelete();
        log.info("FAQ ID: {}가 소프트딜리트되었습니다.", faqId);
    }

    private FaqResponse getFaqResponse(Faq faq) {
        return FaqResponse.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }
}
