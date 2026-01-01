package com.expensetracker.repository;

import com.expensetracker.model.RefreshTokens;
import com.expensetracker.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens,Long> {
    Optional<RefreshTokens> findByToken(String refreshToken);
}
