package com.umc.banddy.domain.band.bookmark.converter;

import com.umc.banddy.domain.band.bookmark.domain.mapping.BandBookmark;
import com.umc.banddy.domain.band.bookmark.web.dto.BandBookmarkResponse;

import java.util.List;
import java.util.stream.Collectors;

public class BandBookmarkConverter {

    public static BandBookmarkResponse toResponse(BandBookmark bookmark) {
        return BandBookmarkResponse.builder()
                .bandId(bookmark.getBand().getId())
                .name(bookmark.getBand().getName())
                .imageUrl(bookmark.getBand().getProfileImageUrl())
                .status(bookmark.getBand().getStatus())
                .build();
    }

    public static List<BandBookmarkResponse> toResponseList(List<BandBookmark> bookmarks) {
        return bookmarks.stream()
                .map(BandBookmarkConverter::toResponse)
                .collect(Collectors.toList());
    }
}
