package com.umc.banddy.domain.auth.repository;

import com.umc.banddy.domain.auth.domain.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlackList, Long> {

    Optional<TokenBlackList> findByToken(String token);

    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenBlackList t WHERE t.expiredAt < :now")
    void deleteAllByExpiredAtBefore(LocalDateTime now);
}
