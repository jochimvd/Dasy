package xyz.vandijck.safety.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.repository.RefreshTokenRepository;
import xyz.vandijck.safety.backend.repository.ResetTokenRepository;

import java.util.Date;

/**
 * Class containing jobs that must be executed at a certain time intervals
 */
@Component
public class ScheduledJobs {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;


    /**
     * Clears the database of expired tokens every day at 2:00 A.M.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void clearExpiredTokens(){
        Date now = new Date();
        refreshTokenRepository.deleteByExpirationBefore(now);
        resetTokenRepository.deleteByExpirationBefore(now);
    }
}
