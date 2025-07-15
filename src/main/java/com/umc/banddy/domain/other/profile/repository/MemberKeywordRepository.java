package com.umc.banddy.domain.other.profile.repository;

import java.util.List;

import com.umc.banddy.domain.mypage.profile.domain.mapping.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, Long> {
    List<MemberKeyword> findByMemberId(Long memberId);
}
