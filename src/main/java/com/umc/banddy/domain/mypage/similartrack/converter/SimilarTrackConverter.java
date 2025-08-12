package com.umc.banddy.domain.mypage.similartrack.converter;

import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.mypage.similartrack.web.dto.SimilarTrackResponse;

public class SimilarTrackConverter {

    // 트랙 리스트
    public static SimilarTrackResponse toResponse(Track track) {
        return SimilarTrackResponse.builder()
                .trackId(track.getId())
                .title(track.getTitle())
                .artist(track.getArtist())
                .album(track.getAlbum())
                .imageUrl(track.getImageUrl())
                .build();
    }

}
