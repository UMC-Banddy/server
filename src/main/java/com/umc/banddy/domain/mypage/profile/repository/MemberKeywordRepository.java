package com.umc.banddy.domain.mypage.profile.repository;

import com.umc.banddy.domain.mypage.profile.domain.mapping.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, Long> {
    List<MemberKeyword> findByMemberId(Long memberId);
}
