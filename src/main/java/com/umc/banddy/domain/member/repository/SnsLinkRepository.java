package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.SnsLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnsLinkRepository extends JpaRepository<SnsLink, Long> {
}
