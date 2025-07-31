package com.umc.banddy.domain.other.profile.converter;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.album.converter.AlbumConverter;
import com.umc.banddy.domain.music.album.domain.MemberAlbum;
import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.member.domain.mapping.MemberGenre;
import com.umc.banddy.domain.other.profile.domain.mapping.*;
import com.umc.banddy.domain.other.profile.web.dto.MemberTagResponse;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import com.umc.banddy.domain.other.profile.web.dto.SavedTrackResponse;
import com.umc.banddy.domain.member.domain.mapping.MemberKeyword;
import com.umc.banddy.domain.member.domain.mapping.MemberSession;
import java.util.List;

public class OtherProfileConverter {

    public static OtherProfileResponse toDto(
            Member member,
            List<MemberTag> tags,
            List<MemberSession> sessions,
            List<MemberArtist> artists,
            List<MemberKeyword> keywords,
            List<MemberGenre> genres,
            String instagramUrl,
            String youtubeUrl,
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
                .region(member.getRegion())
                .tags(tags.stream().map(MemberTag::getTag).toList())
                .sessions(sessions.stream()
                        .map(s -> new OtherProfileResponse.Session(
                                s.getSession().getName(),
                                s.getSession().getIcon()
                        ))
                        .toList())
                .favoriteArtists(artists.stream()
                        .map(a -> new OtherProfileResponse.Artist(
                                a.getArtist().getName(),
                                a.getArtist().getImageUrl()))
                        .toList())
                .traits(keywords.stream()
                        .map(k -> k.getKeyword().getContent())
                        .toList())
                .genres(genres.stream()
                        .map(g -> g.getGenre().getName())
                        .toList())
                .instagramUrl(instagramUrl)
                .youtubeUrl(youtubeUrl)
                .isFriend(isFriend)
                .isBlocked(isBlocked)
                .canRequestChat(canRequestChat)
                .build();
    }

    // 저장한 곡 변환
    public static List<SavedTrackResponse> toSavedTrackDto(List<MemberTrack> tracks) {
        return tracks.stream()
                .map(t -> SavedTrackResponse.builder()
                        .trackId(t.getTrack().getId())
                        .title(t.getTrack().getTitle())
                        .artist(t.getTrack().getArtist())
                        .imageUrl(t.getTrack().getImageUrl())
                        .externalUrl(t.getTrack().getExternalUrl())
                        .build())
                .toList();
    }

    public static MemberTagResponse toMemberTagResponse(Long memberId, List<MemberTag> tags) {
        List<String> tagList = tags.stream()
                .map(MemberTag::getTag)
                .toList();

        return MemberTagResponse.builder()
                .memberId(memberId)
                .tags(tagList)
                .build();
    }

    // 저장 앨범 AlbumResponseDto 변환
    public static List<AlbumResponseDto> toAlbumResponseDtoList(List<MemberAlbum> memberAlbums) {
        return memberAlbums.stream()
                .map(ma -> AlbumConverter.toAlbumResponseDto(ma.getAlbum(), ma.getId()))
                .toList();
    }
}
