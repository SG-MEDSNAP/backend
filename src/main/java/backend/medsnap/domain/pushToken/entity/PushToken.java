package backend.medsnap.domain.pushToken.entity;

import backend.medsnap.domain.user.entity.User;
import backend.medsnap.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "push_tokens",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "token"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING) // Enum 타입을 DB에 문자열로 저장
    @Column(nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private Boolean isActive = true;

    private String lastError;

    @Builder
    public PushToken(User user, String token, Platform platform, String provider, Boolean isActive, String lastError) {
        this.user = user;
        this.token = token;
        this.platform = platform;
        this.provider = provider;
        this.isActive = isActive;
        this.lastError = lastError;
    }

    public void reactivate() {
        this.isActive = true;
        this.lastError = null;
    }

    public void deactivate(String error) {
        this.isActive = false;
        this.lastError = error;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public void updatePlatform(Platform platform) {
        this.platform = platform;
    }
}
