package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.Keyword;
import com.umc.banddy.domain.member.enums.KeywordCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByContent(String content);
    Optional<Keyword> findByContentAndCategory(String content, KeywordCategory category);
}
