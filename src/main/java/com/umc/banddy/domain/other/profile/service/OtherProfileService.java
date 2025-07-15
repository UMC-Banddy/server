package com.umc.banddy.domain.other.profile.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.mypage.profile.domain.mapping.MemberKeyword;
import com.umc.banddy.domain.other.profile.converter.OtherProfileConverter;
import com.umc.banddy.domain.other.profile.domain.mapping.*;
import com.umc.banddy.domain.other.profile.repository.*;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
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

    public OtherProfileResponse getOtherProfile(Long loginMemberId, Long targetMemberId) {
        Member member = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<MemberTag> tags = memberTagRepository.findByMemberId(targetMemberId);
        List<MemberSession> sessions = memberSessionRepository.findByMemberId(targetMemberId);
        List<MemberArtist> artists = memberArtistRepository.findByMemberId(targetMemberId);
        List<MemberKeyword> keywords = memberKeywordRepository.findByMemberId(targetMemberId);
        List<MemberSns> snsList = memberSNSRepository.findById2(targetMemberId);

        MemberSns instagram = snsList.stream().filter(s -> "instagram".equalsIgnoreCase(s.getSnsName())).findFirst().orElse(null);
        MemberSns youtube = snsList.stream().filter(s -> "youtube".equalsIgnoreCase(s.getSnsName())).findFirst().orElse(null);

        boolean isFriend = friendRepository.findByMemberIdAndFriendshipId(loginMemberId, targetMemberId).isPresent();
        boolean isBlocked = false; // TODO: 차단 기능 개발 시 변경
        boolean canRequestChat = !isBlocked && !loginMemberId.equals(targetMemberId);

        return OtherProfileConverter.toDto(member, tags, sessions, artists, keywords, instagram, youtube, isFriend, isBlocked, canRequestChat);
    }
}
