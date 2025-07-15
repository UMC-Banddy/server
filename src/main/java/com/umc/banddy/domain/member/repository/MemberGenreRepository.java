package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.mapping.MemberGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberGenreRepository extends JpaRepository<MemberGenre, Long> {
}
