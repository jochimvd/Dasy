package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.backend.entity.ResetToken;
import xyz.vandijck.safety.backend.entity.User;

import java.util.Date;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    void deleteByUser(User user);
    ResetToken findByUser(User user);
    void deleteByExpirationBefore(Date time);

}
