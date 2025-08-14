package com.umc.banddy.domain.mypage.similarartist.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.mypage.similarartist.converter.SimilarArtistConverter;
import com.umc.banddy.domain.mypage.similarartist.web.dto.SimilarArtistResponse;
import com.umc.banddy.domain.mypage.similarartist.web.dto.ArtistSuggestionQuestionResponse;
import com.umc.banddy.global.util.MemberSimilarityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimilarArtistService {

    private final MemberRepository memberRepository;
    private final MemberArtistRepository memberArtistRepository;
    private final MemberSimilarityUtil similarityUtil;

    // 유사한 유저들이 저장한 아티스트 상위 5개
    public List<SimilarArtistResponse> getArtistsSavedBySimilarUsers(Long loginMemberId) {
        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<Member> similarMembers = similarityUtil.findSimilarMembers(loginMember);
        List<Artist> artists = memberArtistRepository.findTopSavedArtistsByMembers(similarMembers, loginMemberId, 5);

        return artists.stream()
                .map(SimilarArtistConverter::toResponse)
                .collect(Collectors.toList());
    }

    // 한 줄 질문
    public ArtistSuggestionQuestionResponse getArtistSearchHintQuestion(Long loginMemberId) {
        Member me = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<SimilarArtistResponse> list = getArtistsSavedBySimilarUsers(loginMemberId);

        String artistName = list.isEmpty() ? null : list.get(0).getName(); // 하나만 집는다
        String question = (artistName != null && !artistName.isBlank())
                ? me.getNickname() + " 님이 좋아하는 " + artistName + "의 곡은 어때요?"
                : me.getNickname() + " 님이 좋아할 만한 아티스트를 찾아볼까요?";

        return ArtistSuggestionQuestionResponse.builder()
                .question(question)
                .artistName(artistName)
                .memberNickname(me.getNickname())
                .build();
    }

}