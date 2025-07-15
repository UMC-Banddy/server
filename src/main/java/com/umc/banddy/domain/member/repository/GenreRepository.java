package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(String name);
    List<Genre> findByNameContainingIgnoreCase(String keyword);
}