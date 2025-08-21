package com.umc.banddy.domain.other.profile.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.other.profile.domain.Tag;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
    List<MemberTag> findByMemberId(Long memberId);

    // 여러 회원의 태그를 한 번에 로딩
    List<MemberTag> findByMemberIdIn(Collection<Long> memberIds);

    void deleteByMemberId(Long memberId);

    @Query("select mt.tag.id from MemberTag mt where mt.member.id = :memberId")
    List<Long> findTagIdsByMemberId(@Param("memberId") Long memberId);

    // 지울 tag_id들만 선별 삭제
    void deleteByMemberIdAndTagIdIn(Long memberId, Collection<Long> tagIds);

    boolean existsByMemberAndTag(Member member, Tag tag);

    // 현재 회원을 제외한, 태그가 존재하는 모든 회원 조회 (유사도 비교용)
    @Query("""
    select distinct m
      from MemberTag mt
      join mt.member m
     where m.id <> :excludeId
""")
    List<Member> findAllMembersExcept(@Param("excludeId") Long excludeId);
}
