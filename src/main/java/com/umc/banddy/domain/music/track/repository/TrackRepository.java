package com.umc.banddy.domain.music.track.repository;

import com.umc.banddy.domain.music.track.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findBySpotifyId(String spotifyId);
}
