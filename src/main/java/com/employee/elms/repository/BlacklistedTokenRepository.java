package com.employee.elms.repository;

import com.employee.elms.entity.BlacklistedToken;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);
    
    @Transactional
    void deleteByBlacklistedAtBefore(LocalDateTime dateTime);
}