package com.umc.banddy.domain.other.profile.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.other.profile.converter.OtherProfileConverter;
import com.umc.banddy.domain.other.profile.domain.mapping.*;
import com.umc.banddy.domain.other.profile.repository.*;
import com.umc.banddy.domain.other.profile.web.dto.MemberTagResponse;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import com.umc.banddy.domain.other.profile.web.dto.SavedTrackResponse;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import com.umc.banddy.domain.other.profile.repository.MemberTagRepository;
import com.umc.banddy.domain.member.repository.MemberKeywordRepository;
import com.umc.banddy.domain.member.domain.mapping.MemberKeyword;
import com.umc.banddy.domain.member.repository.MemberSessionRepository;
import com.umc.banddy.domain.member.domain.mapping.MemberSession;
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
    private final MemberSnsRepository memberSNSRepository;
    private final FriendRepository friendRepository;
    private final MemberTrackRepository memberTrackRepository;

    // ✅ 상대방 프로필 조회
    public OtherProfileResponse getOtherProfile(Long loginMemberId, Long targetMemberId) {
        Member member = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<MemberTag> tags = memberTagRepository.findByMemberId(targetMemberId);
        List<MemberSession> sessions = memberSessionRepository.findByMemberId(targetMemberId);
        List<MemberArtist> artists = memberArtistRepository.findByMemberId(targetMemberId);
        List<MemberKeyword> keywords = memberKeywordRepository.findByMemberId(targetMemberId);
        List<MemberSns> snsList = memberSNSRepository.findByMemberId(targetMemberId);

        MemberSns instagram = snsList.stream().filter(s -> "instagram".equalsIgnoreCase(s.getSnsName())).findFirst().orElse(null);
        MemberSns youtube = snsList.stream().filter(s -> "youtube".equalsIgnoreCase(s.getSnsName())).findFirst().orElse(null);

        boolean isFriend = friendRepository.findByMemberIdAndFriendshipId(loginMemberId, targetMemberId).isPresent();
        boolean isBlocked = false; // TODO: 차단 기능 개발 시 변경
        boolean canRequestChat = !isBlocked && !loginMemberId.equals(targetMemberId);

        return OtherProfileConverter.toDto(member, tags, sessions, artists, keywords, instagram, youtube, isFriend, isBlocked, canRequestChat);
    }

    // ✅ 저장한 곡 목록 조회
    public List<SavedTrackResponse> getSavedTracks(Long memberId) {
        return memberTrackRepository.findByMemberId(memberId).stream()
                .map(memberTrack -> {
                    var track = memberTrack.getTrack();
                    return SavedTrackResponse.builder()
                            .trackId(track.getId())
                            .title(track.getTitle())
                            .artist(track.getArtist()) // Track 엔티티에 있다고 가정
                            .imageUrl(track.getImageUrl())
                            .externalUrl(track.getExternalUrl())
                            .build();
                })
                .toList();
    }

    public MemberTagResponse getTagsByMemberId(Long memberId) {
        List<MemberTag> tags = memberTagRepository.findByMemberId(memberId);
        return OtherProfileConverter.toMemberTagResponse(memberId, tags); // ✅
    }
}
