package backend.medsnap.domain.user.entity;

import jakarta.persistence.*;

import backend.medsnap.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "social_accounts",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_social_provider_user",
                    columnNames = {"provider", "provider_user_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String providerUserId; // sub

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @Builder
    public SocialAccount(String providerUserId, Provider provider, User user) {
        this.providerUserId = providerUserId;
        this.provider = provider;
        this.user = user;
    }
}
