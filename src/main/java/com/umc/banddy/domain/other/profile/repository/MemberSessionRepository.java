package com.umc.banddy.domain.other.profile.repository;

import com.umc.banddy.domain.other.profile.domain.mapping.MemberSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSessionRepository extends JpaRepository<MemberSession, Long> {
    List<MemberSession> findByMemberId(Long memberId);
}
