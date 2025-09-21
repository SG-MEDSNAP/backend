package backend.medsnap.domain.user.repository;

import backend.medsnap.domain.user.entity.Provider;
import backend.medsnap.domain.user.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(Provider provider, String providerUserId);
}
