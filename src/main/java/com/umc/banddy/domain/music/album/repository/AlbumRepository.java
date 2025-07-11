package com.umc.banddy.domain.music.album.repository;

import com.umc.banddy.domain.music.album.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findBySpotifyId(String spotifyId);
}
