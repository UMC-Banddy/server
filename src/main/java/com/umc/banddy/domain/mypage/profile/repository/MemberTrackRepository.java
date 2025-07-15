package com.umc.banddy.domain.mypage.profile.repository;

import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberTrackRepository extends JpaRepository<MemberTrack, Long> {
    List<MemberTrack> findTop3ByMemberIdOrderByCreatedAtDesc(Long memberId);
}

