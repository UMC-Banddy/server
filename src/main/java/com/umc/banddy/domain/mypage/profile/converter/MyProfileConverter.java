package com.umc.banddy.domain.mypage.profile.converter;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;

import java.util.List;
import java.util.stream.Collectors;

public class MyProfileConverter {

    public static MyProfileResponse toMyProfileResponse(
            Member member,
            List<MemberTag> tags,
            List<MemberTrack> savedTracks,
            List<MyProfileResponse.SessionInfo> sessions,
            List<String> interestedGenres
    ) {
        List<String> tagNames = tags.stream()
                .map(MemberTag::getTagName)
                .collect(Collectors.toList());

        List<MyProfileResponse.SavedTrack> trackResponses = savedTracks.stream()
                .map(t -> new MyProfileResponse.SavedTrack(
                        t.getTrack().getTitle(),
                        t.getTrack().getImageUrl(),
                        t.getTrack().getExternalUrl()
                ))
                .collect(Collectors.toList());

        return MyProfileResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .bio(member.getBio())
                .age(member.getAge())
                .gender(member.getGender() != null ? member.getGender().name() : null)
                .region(member.getRegion())
                .sessions(sessions)
                .interestedGenres(interestedGenres)
                .tags(tagNames)
                .savedTracks(trackResponses)
                .build();
    }
}
