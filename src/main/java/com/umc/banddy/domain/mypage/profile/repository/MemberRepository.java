package com.umc.banddy.domain.mypage.profile.repository;

import com.umc.banddy.domain.mypage.profile.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
