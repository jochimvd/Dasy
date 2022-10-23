package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.vandijck.safety.backend.entity.RefreshToken;
import xyz.vandijck.safety.backend.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByUser(User user);
    List<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByToken(byte[] token);
    void deleteByToken(byte[] token);
    void deleteByTokenNotAndUser(byte[] token, User user);
    void deleteByExpirationBefore(Date time);

}
