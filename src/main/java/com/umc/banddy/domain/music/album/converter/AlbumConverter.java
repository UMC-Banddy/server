package com.umc.banddy.domain.music.album.converter;

import com.umc.banddy.domain.music.album.domain.Album;
import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import com.umc.banddy.domain.music.album.web.dto.AlbumToggleResponseDto;

public class AlbumConverter {

    public static Album toAlbumFromSpotify(
            String spotifyId,
            String name,
            String artist,
            String imageUrl,
            String externalUrl
    ) {
        return Album.builder()
                .spotifyId(spotifyId)
                .name(name)
                .artist(artist)
                .imageUrl(imageUrl)
                .externalUrl(externalUrl)
                .build();
    }

    public static AlbumResponseDto toAlbumResponseDto(Album album, Long memberAlbumId) {
        return AlbumResponseDto.builder()
                .albumId(album.getId())
                .spotifyId(album.getSpotifyId())
                .name(album.getName())
                .artist(album.getArtist())
                .imageUrl(album.getImageUrl())
                .externalUrl(album.getExternalUrl())
                .build();
    }

    public static AlbumToggleResponseDto toAlbumToggleResponseDto(Album album, boolean isSaved) {
        return AlbumToggleResponseDto.builder()
                .albumId(album.getId())
                .spotifyId(album.getSpotifyId())
                .isSaved(isSaved)
                .build();
    }
}
