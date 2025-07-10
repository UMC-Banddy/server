package com.umc.banddy.domain.music.track.converter;

import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;

public class TrackConverter {

    // Spotify API에서 받아온 정보로 Track 엔티티 생성
    public static Track toTrackFromSpotify(
            String spotifyId,
            String title,
            String artist,
            String album,
            String duration,
            String imageUrl
    ) {
        return Track.builder()
                .spotifyId(spotifyId)
                .title(title)
                .artist(artist)
                .album(album)
                .duration(duration)
                .imageUrl(imageUrl)
                .build();
    }

    // Track 엔티티 -> TrackResponseDto.TrackResultDto 변환
    public static TrackResponseDto.TrackResultDto toTrackResultDto(Track track, Long memberTrackId) {
        return TrackResponseDto.TrackResultDto.builder()
                .trackId(track.getId())
                .spotifyId(track.getSpotifyId())
                .title(track.getTitle())
                .artist(track.getArtist())
                .album(track.getAlbum())
                .duration(track.getDuration())
                .imageUrl(track.getImageUrl())
                .memberTrackId(memberTrackId)
                .build();
    }
}
