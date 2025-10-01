package backend.medsnap.domain.pushToken.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.pushToken.entity.PushToken;
import backend.medsnap.domain.user.entity.User;

@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByUser(User user);

    /** 워커용: 활성 토큰 조회 */
    @Query(
            "SELECT pt "
                    + "FROM PushToken pt "
                    + "WHERE pt.user.id = :userId "
                    + "AND pt.isActive = true")
    List<PushToken> findActiveTokensByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE PushToken pt "
                    + "SET pt.isActive = false, pt.lastError = :reason "
                    + "WHERE pt.token IN :tokens")
    int deactivateAllByTokenIn(
            @Param("tokens") Collection<String> tokens, @Param("reason") String reason);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE PushToken pt "
                    + "SET pt.isActive = false, pt.lastError = :reason "
                    + "WHERE pt.user.id = :userId")
    int deactivateAllByUserId(@Param("userId") Long userId, @Param("reason") String reason);
}
