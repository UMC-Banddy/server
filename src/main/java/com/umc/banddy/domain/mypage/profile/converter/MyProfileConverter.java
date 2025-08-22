package com.umc.banddy.domain.mypage.profile.converter;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.domain.mapping.MemberKeyword;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;

import java.util.List;
import java.util.stream.Collectors;

public class MyProfileConverter {

    // (A) 조회: 기존 태그(List<MemberTag>)
    public static MyProfileResponse toMyProfileResponse(
            Member member,
            List<MemberTag> tags,
            List<MemberTrack> savedTracks,
            List<MyProfileResponse.SessionInfo> sessions,
            List<String> interestedGenres
    ) {
        List<String> names = tags == null ? List.of()
                : tags.stream()
                .map(MemberTag::getTagName)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());
        return build(member, names, savedTracks, sessions, interestedGenres);
    }

    // (B) 수정 응답: 키워드(List<MemberKeyword>)
    public static MyProfileResponse toMyProfileResponse(
            Member member,
            List<MemberKeyword> keywords,
            List<MemberTrack> savedTracks,
            List<MyProfileResponse.SessionInfo> sessions,
            List<String> interestedGenres,
            boolean fromKeyword // 시그니처 충돌 방지용
    ) {
        List<String> names = keywords == null ? List.of()
                : keywords.stream()
                .map(mk -> mk.getKeyword() != null ? mk.getKeyword().getContent() : null)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());
        return build(member, names, savedTracks, sessions, interestedGenres);
    }

    // 공통 빌더 (응답 필드명은 기존처럼 tags)
    private static MyProfileResponse build(
            Member member,
            List<String> tagOrKeywordNames,
            List<MemberTrack> savedTracks,
            List<MyProfileResponse.SessionInfo> sessions,
            List<String> interestedGenres
    ) {
        List<MyProfileResponse.SavedTrack> trackResponses = savedTracks == null ? List.of()
                : savedTracks.stream()
                .map(t -> new MyProfileResponse.SavedTrack(
                        t.getTrack().getTitle(),
                        t.getTrack().getImageUrl(),
                        t.getTrack().getExternalUrl()
                ))
                .toList();

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
                .tags(tagOrKeywordNames)   // ← 응답 DTO의 필드명이 tags라서 그대로 사용
                .savedTracks(trackResponses)
                .build();
    }
}
