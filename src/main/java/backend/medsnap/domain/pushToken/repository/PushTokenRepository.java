package backend.medsnap.domain.pushToken.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.pushToken.entity.PushToken;
import backend.medsnap.domain.user.entity.User;

@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByUser(User user);
}
