package backend.medsnap.domain.pushToken.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import backend.medsnap.domain.user.entity.User;
import backend.medsnap.global.entity.BaseEntity;
import lombok.*;

@Entity
@Table(
        name = "push_tokens",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "token"})})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE push_tokens SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class PushToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String provider;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    private String lastError;

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
