package com.umc.banddy.domain.mypage.similarartist.converter;

import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.mypage.similarartist.web.dto.SimilarArtistResponse;

public class SimilarArtistConverter {

    public static SimilarArtistResponse toResponse(Artist artist) {
        return SimilarArtistResponse.builder()
                .artistId(artist.getId())
                .name(artist.getName())
                .imageUrl(artist.getImageUrl())
                .build();
    }
}

