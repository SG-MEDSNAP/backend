package backend.medsnap.domain.user.entity;

import backend.medsnap.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDate birthday;

    private String phone;

    private String caregiverPhone;

    private String accessToken;

    private String refreshToken;

    private Boolean isPushConsent;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @Builder
    public User(LocalDate birthday, String phone, String caregiverPhone, Boolean isPushConsent) {
        this.role = Role.USER;
        this.birthday = birthday;
        this.phone = phone;
        this.caregiverPhone = caregiverPhone;
        this.isPushConsent = isPushConsent;
    }

    // 자체 JWT 업데이트
    public void updateTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
