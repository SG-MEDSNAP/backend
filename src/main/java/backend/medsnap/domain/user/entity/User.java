package backend.medsnap.domain.user.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    private String phone;

    // private String caregiverPhone;

    private String refreshToken;

    private Boolean isPushConsent;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medication> medications = new ArrayList<>();

    @Builder
    public User(
            String name,
            LocalDate birthday,
            String phone,
            Boolean isPushConsent) {
        this.role = Role.USER;
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        // this.caregiverPhone = caregiverPhone;
        this.isPushConsent = isPushConsent;
    }

    public void updateProfile(
            String name,
            LocalDate birthday,
            String phone,
            Boolean isPushConsent) {
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        // this.caregiverPhone = caregiverPhone;
        this.isPushConsent = isPushConsent;
    }

    // Refresh Token 업데이트
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 소프트 딜리트
    @Override
    public void softDelete() {
        // provider_user_id를 null로 설정
        socialAccounts.forEach(SocialAccount::clearProviderUserId);
        
        super.softDelete();
    }
}
