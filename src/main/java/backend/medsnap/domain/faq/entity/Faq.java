package backend.medsnap.domain.faq.entity;

import jakarta.persistence.*;

import backend.medsnap.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "faqs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FaqCategory category;

    @Builder
    public Faq(String question, String answer, FaqCategory category) {
        this.question = question;
        this.answer = answer;
        this.category = category;
    }
}
