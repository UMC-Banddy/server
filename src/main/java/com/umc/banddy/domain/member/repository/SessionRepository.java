package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByName(String name);

    List<Session> findByNameIn(Collection<String> names);
}
