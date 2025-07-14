package com.umc.banddy.domain.music.artist.converter;

import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.artist.web.dto.ArtistRequestDto;
import com.umc.banddy.domain.music.artist.web.dto.ArtistResponseDto;

public class ArtistConverter {

    public static Artist toArtist(ArtistRequestDto dto) {
        return Artist.builder()
                .spotifyId(dto.getSpotifyId())
                .build();
    }

    public static ArtistResponseDto toArtistResponseDto(Artist artist, Long memberArtistId) {
        return ArtistResponseDto.builder()
                .artistId(artist.getId())
                .spotifyId(artist.getSpotifyId())
                .name(artist.getName())
                .genre(artist.getGenre())
                .imageUrl(artist.getImageUrl())
                .memberArtistId(memberArtistId)
                .build();
    }
}

