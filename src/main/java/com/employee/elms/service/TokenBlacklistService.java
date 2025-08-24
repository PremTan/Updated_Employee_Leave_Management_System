package com.employee.elms.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.employee.elms.entity.BlacklistedToken;
import com.employee.elms.repository.BlacklistedTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository repository;

    public void blacklistToken(String token) {
        if (!repository.existsByToken(token)) {
            BlacklistedToken t = new BlacklistedToken();
            t.setToken(token);
            repository.save(t);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return repository.existsByToken(token);
    }
    
    // Scheduled cleanup every 10 minutes
    @Scheduled(fixedRate = 10 * 60 * 1000) // 10 minutes
    public void removeExpiredTokens() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        repository.deleteByBlacklistedAtBefore(oneHourAgo);
    }
}
