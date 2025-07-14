package com.umc.banddy.domain.music.artist.repository;

import com.umc.banddy.domain.music.artist.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findBySpotifyId(String spotifyId);
}
