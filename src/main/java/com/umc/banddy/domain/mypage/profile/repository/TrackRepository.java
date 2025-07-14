package com.umc.banddy.domain.mypage.profile.repository;

import com.umc.banddy.domain.mypage.profile.domain.mapping.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {
}
