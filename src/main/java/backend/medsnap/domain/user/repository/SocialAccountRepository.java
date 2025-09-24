package backend.medsnap.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.user.entity.Provider;
import backend.medsnap.domain.user.entity.SocialAccount;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(
            Provider provider, String providerUserId);
}
