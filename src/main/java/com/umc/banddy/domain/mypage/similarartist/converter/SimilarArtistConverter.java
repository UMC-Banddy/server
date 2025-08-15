package com.umc.banddy.domain.mypage.similarartist.converter;

import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.mypage.similarartist.web.dto.SimilarArtistResponse;
import com.umc.banddy.domain.mypage.similarartist.web.dto.ArtistSuggestionQuestionResponse;

public class SimilarArtistConverter {

    public static SimilarArtistResponse toResponse(Artist artist) {
        return SimilarArtistResponse.builder()
                .artistId(artist.getId())
                .name(artist.getName())
                .imageUrl(artist.getImageUrl())
                .build();
    }

    // 한 줄 추천 질문
    public static ArtistSuggestionQuestionResponse toSuggestionQuestionResponse(
            String question,
            String artistName,
            String memberNickname
    ) {
        return ArtistSuggestionQuestionResponse.builder()
                .question(question)
                .artistName(artistName)
                .memberNickname(memberNickname)
                .build();
    }
}
