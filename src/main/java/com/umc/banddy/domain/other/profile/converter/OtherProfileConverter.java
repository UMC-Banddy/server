package com.umc.banddy.domain.other.profile.converter;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.mypage.profile.domain.mapping.MemberKeyword;
import com.umc.banddy.domain.other.profile.domain.mapping.*;
import com.umc.banddy.domain.other.profile.web.dto.MemberTagResponse;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import com.umc.banddy.domain.other.profile.web.dto.SavedTrackResponse;

import java.util.List;
import java.util.stream.Collectors;

public class OtherProfileConverter {

    public static OtherProfileResponse toDto(
            Member member,
            List<MemberTag> tags,
            List<MemberSession> sessions,
            List<MemberArtist> artists,
            List<MemberKeyword> keywords,
            MemberSns instagram,
            MemberSns youtube,
            boolean isFriend,
            boolean isBlocked,
            boolean canRequestChat
    ) {
        return OtherProfileResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .bio(member.getBio())
                .profileImageUrl(member.getProfileImageUrl())
                .age(member.getAge())
                .gender(member.getGender().name())
                .tags(tags.stream().map(MemberTag::getTag).collect(Collectors.toList()))
                .sessions(sessions.stream()
                        .map(s -> new OtherProfileResponse.Session(
                                s.getSession().getName(),
                                s.getSession().getIcon()
                        ))
                        .collect(Collectors.toList()))
                .favoriteArtists(artists.stream()
                        .map(a -> new OtherProfileResponse.Artist(a.getArtist().getName(), a.getArtist().getImageUrl()))
                        .collect(Collectors.toList()))
                .traits(keywords.stream().map(k -> k.getKeyword().getContent()).collect(Collectors.toList()))
                .instagramUrl(instagram != null ? instagram.getSnsUrl() : null)
                .youtubeUrl(youtube != null ? youtube.getSnsUrl() : null)
                .isFriend(isFriend)
                .isBlocked(isBlocked)
                .canRequestChat(canRequestChat)
                .build();
    }

    public static List<SavedTrackResponse> toSavedTrackDto(List<MemberTrack> tracks) {
        return tracks.stream()
                .map(t -> SavedTrackResponse.builder()
                        .trackId(t.getTrack().getId())
                        .title(t.getTrack().getTitle())
                        .artist(t.getTrack().getArtist())
                        .imageUrl(t.getTrack().getImageUrl())
                        .externalUrl(t.getTrack().getExternalUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public static MemberTagResponse toMemberTagResponse(Long memberId, List<MemberTag> tags) {
        List<String> tagList = tags.stream()
                .map(MemberTag::getTag)
                .collect(Collectors.toList());

        return MemberTagResponse.builder()
                .memberId(memberId)
                .tags(tagList)
                .build();
    }
}
