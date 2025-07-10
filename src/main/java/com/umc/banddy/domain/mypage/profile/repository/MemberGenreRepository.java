package com.umc.banddy.domain.mypage.profile.repository;

import com.umc.banddy.domain.mypage.profile.domain.mapping.MemberGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberGenreRepository extends JpaRepository<MemberGenre, Long> {
    List<MemberGenre> findByMemberId(Long memberId); // ← 예시
}
