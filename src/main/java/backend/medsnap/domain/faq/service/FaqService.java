package backend.medsnap.domain.faq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.faq.dto.request.FaqRequest;
import backend.medsnap.domain.faq.dto.response.FaqResponse;
import backend.medsnap.domain.faq.entity.Faq;
import backend.medsnap.domain.faq.repository.FaqRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    @Transactional
    public FaqResponse createFaq(FaqRequest request) {
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
}
