package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByName(String name);
}
