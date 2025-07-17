package com.umc.banddy.domain.other.profile.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
    List<MemberTag> findByMember(Member member);

    List<MemberTag> findByMemberId(Long memberId);

    // 현재 회원을 제외한, 태그가 존재하는 모든 회원 조회 (유사도 비교용)
    @Query("SELECT DISTINCT mt.member FROM MemberTag mt WHERE mt.member.id <> :excludeId")
    List<Member> findAllMembersExcept(@Param("excludeId") Long excludeId);
}
