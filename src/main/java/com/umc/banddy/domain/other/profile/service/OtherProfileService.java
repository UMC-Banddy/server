package com.umc.banddy.domain.other.profile.service;

import com.umc.banddy.domain.friend.repository.FriendRepository;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.domain.SnsLink;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.member.repository.SnsLinkRepository;
import com.umc.banddy.domain.member.domain.mapping.MemberGenre;
import com.umc.banddy.domain.member.repository.MemberGenreRepository;
import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import com.umc.banddy.domain.music.album.repository.MemberAlbumRepository;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.other.profile.converter.OtherProfileConverter;
import com.umc.banddy.domain.other.profile.web.dto.MemberTagResponse;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import com.umc.banddy.domain.other.profile.web.dto.SavedTrackResponse;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import com.umc.banddy.domain.other.profile.repository.MemberTagRepository;
import com.umc.banddy.domain.member.repository.MemberKeywordRepository;
import com.umc.banddy.domain.member.domain.mapping.MemberKeyword;
import com.umc.banddy.domain.member.repository.MemberSessionRepository;
import com.umc.banddy.domain.member.domain.mapping.MemberSession;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OtherProfileService {

    private final MemberRepository memberRepository;
    private final MemberTagRepository memberTagRepository;
    private final MemberSessionRepository memberSessionRepository;
    private final MemberArtistRepository memberArtistRepository;
    private final MemberKeywordRepository memberKeywordRepository;
    private final MemberGenreRepository memberGenreRepository;
    private final SnsLinkRepository snsLinkRepository;
    private final FriendRepository friendRepository;
    private final MemberTrackRepository memberTrackRepository;
    private final MemberAlbumRepository memberAlbumRepository;

    // 상대방 프로필 조회
    public OtherProfileResponse getOtherProfile(Long loginMemberId, Long targetMemberId) {
        Member member = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<MemberTag> tags = memberTagRepository.findByMemberId(targetMemberId);
        List<MemberSession> sessions = memberSessionRepository.findByMemberId(targetMemberId);
        List<MemberArtist> artists = memberArtistRepository.findByMemberId(targetMemberId);
        List<MemberKeyword> keywords = memberKeywordRepository.findByMemberId(targetMemberId);
        List<MemberGenre> genres = memberGenreRepository.findByMemberId(targetMemberId);

        // SNS 링크 조회
        List<SnsLink> snsLinks = snsLinkRepository.findAll();
        String instagramUrl = snsLinks.stream()
                .filter(s -> s.getMember().getId().equals(targetMemberId) && s.getPlatform().name().equalsIgnoreCase("INSTAGRAM"))
                .map(SnsLink::getUrl)
                .findFirst()
                .orElse(null);
        String youtubeUrl = snsLinks.stream()
                .filter(s -> s.getMember().getId().equals(targetMemberId) && s.getPlatform().name().equalsIgnoreCase("YOUTUBE"))
                .map(SnsLink::getUrl)
                .findFirst()
                .orElse(null);

        boolean isFriend = !friendRepository
                .findAllBetween(loginMemberId, targetMemberId).isEmpty();

        boolean isBlocked = false;
        boolean canRequestChat = !isBlocked && !loginMemberId.equals(targetMemberId);

        return OtherProfileConverter.toDto(
                member, tags, sessions, artists, keywords, genres,
                instagramUrl, youtubeUrl, isFriend, isBlocked, canRequestChat
        );
    }

    // 상대방 저장 트랙 조회
    public List<SavedTrackResponse> getSavedTracks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        return memberTrackRepository.findAllByMember(member).stream()
                .map(memberTrack -> {
                    var track = memberTrack.getTrack();
                    return SavedTrackResponse.builder()
                            .trackId(track.getId())
                            .title(track.getTitle())
                            .artist(track.getArtist())
                            .imageUrl(track.getImageUrl())
                            .externalUrl(track.getExternalUrl())
                            .build();
                })
                .toList();
    }

    // 상대방 태그 조회
    public MemberTagResponse getTagsByMemberId(Long memberId) {
        List<MemberTag> tags = memberTagRepository.findByMemberId(memberId);
        return OtherProfileConverter.toMemberTagResponse(memberId, tags);
    }

    // 상대방 공개 앨범 조회
    public List<AlbumResponseDto> getSavedAlbums(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        return OtherProfileConverter.toAlbumResponseDtoList(
                memberAlbumRepository.findAllByMemberAndIsPrivateFalse(member)
        );
    }
}
