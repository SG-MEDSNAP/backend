package backend.medsnap.domain.pushToken.repository;

import backend.medsnap.domain.pushToken.entity.PushToken;
import backend.medsnap.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByUserAndToken(User user, String token);
}
