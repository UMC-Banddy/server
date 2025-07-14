package com.umc.banddy.domain.music.search.web.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchAllResult {
    private List<TrackInfo> tracks;
    private List<ArtistInfo> artists;
    private List<AlbumInfo> albums;
}

