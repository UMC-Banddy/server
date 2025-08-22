package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.Keyword;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.domain.mapping.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, Long> {

    // 멤버별 키워드 매핑 조회/삭제
    List<MemberKeyword> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);

    // 조합 단건
    Optional<MemberKeyword> findByMemberAndKeyword(Member member, Keyword keyword);
    void deleteByMemberAndKeyword(Member member, Keyword keyword);

    // 존재 여부 (중복 방지/빠른 체크)
    boolean existsByMemberIdAndKeywordId(Long memberId, Long keywordId);

    // 여러 멤버 한 번에
    List<MemberKeyword> findByMemberIdIn(Collection<Long> memberIds);

    // 편의: 키워드 엔티티만 뽑기
    @Query("select mk.keyword from MemberKeyword mk where mk.member.id = :memberId")
    List<Keyword> findKeywordsByMemberId(@Param("memberId") Long memberId);

    // (선택) 멤버 + 키워드 내용(복수, 대소문자 무시)로 조회
    @Query("""
           select mk 
           from MemberKeyword mk 
           where mk.member.id = :memberId 
             and lower(mk.keyword.content) in :lowerContents
           """)
    List<MemberKeyword> findByMemberIdAndKeywordContentsIgnoreCase(
            @Param("memberId") Long memberId,
            @Param("lowerContents") Collection<String> lowerContents
    );
}
