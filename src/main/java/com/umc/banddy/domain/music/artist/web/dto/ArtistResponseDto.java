package com.umc.banddy.domain.music.artist.web.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistResponseDto {
    private Long artistId;
    private String spotifyId;
    private String name;
    private String genre;
    private String imageUrl;
    private Long memberArtistId; // 저장한 경우에만 값 세팅, 아니면 null
}

