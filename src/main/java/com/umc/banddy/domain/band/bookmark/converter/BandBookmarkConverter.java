package com.umc.banddy.domain.band.bookmark.converter;

import com.umc.banddy.domain.band.bookmark.domain.mapping.BandBookmark;
import com.umc.banddy.domain.band.bookmark.web.dto.BandBookmarkResponse;
import com.umc.banddy.domain.band.profile.domain.mapping.MemberBand;

import java.util.List;
import java.util.stream.Collectors;

public class BandBookmarkConverter {

    public static BandBookmarkResponse toResponse(BandBookmark bookmark, List<String> memberNames, int totalCount) {
        String memberSummary = buildMemberSummary(memberNames, totalCount);

        return BandBookmarkResponse.builder()
                .bandId(bookmark.getBand().getId())
                .name(bookmark.getBand().getName())
                .imageUrl(bookmark.getBand().getProfileImageUrl())
                .status(bookmark.getBand().getStatus())
                .isSoundOn(bookmark.isSoundOn())
                .memberSummary(memberSummary)
                .memberCount(totalCount)
                .build();
    }

    public static List<BandBookmarkResponse> toResponseList(List<BandBookmark> bookmarks) {
        return bookmarks.stream()
                .map(bookmark -> {
                    List<MemberBand> members = bookmark.getBand().getMembers(); // band.getMembers()가 필요
                    List<String> names = members.stream()
                            .map(bm -> bm.getMember().getNickname())
                            .collect(Collectors.toList());
                    return toResponse(bookmark, names.stream().limit(3).toList(), names.size());
                })
                .collect(Collectors.toList());
    }

    private static String buildMemberSummary(List<String> memberNames, int totalCount) {
        if (memberNames.isEmpty()) return "";
        String prefix = String.join(", ", memberNames);
        int rest = totalCount - memberNames.size();
        return rest > 0 ? prefix + " 외 " + rest + "명" : prefix;
    }
}
