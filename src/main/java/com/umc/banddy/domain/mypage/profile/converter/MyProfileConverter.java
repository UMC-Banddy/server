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
            List<MemberTrack> savedTracks
    ) {
        List<String> tagNames = tags.stream()
                .map(MemberTag::getTagName)
                .collect(Collectors.toList());

        List<MyProfileResponse.SavedTrack> trackResponses = savedTracks.stream()
                .map(t -> new MyProfileResponse.SavedTrack(
                        t.getTrack().getTitle(),
                        t.getTrack().getImageUrl()))
                .collect(Collectors.toList());

        return new MyProfileResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getBio(),
                tagNames,
                trackResponses
        );
    }
}
