package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.mapping.MemberSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSessionRepository extends JpaRepository<MemberSession, Long> {
}
