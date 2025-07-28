package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.mapping.MemberSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberSessionRepository extends JpaRepository<MemberSession, Long> {
    List<MemberSession> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
