package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.mapping.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, Long> {
}
