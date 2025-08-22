package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.Keyword;
import com.umc.banddy.domain.member.enums.KeywordCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    // content는 유니크 컬럼
    Optional<Keyword> findByContent(String content);

    // 대소문자 무시 조회 (권장)
    Optional<Keyword> findByContentIgnoreCase(String content);

    // 카테고리 조건 포함 조회
    Optional<Keyword> findByContentAndCategory(String content, KeywordCategory category);

    List<Keyword> findAllByCategory(KeywordCategory category);

    List<Keyword> findAllByContentIn(Collection<String> contents);

    boolean existsByContentIgnoreCase(String content);
}
