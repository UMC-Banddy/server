package com.umc.banddy.domain.mypage.similarartist.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.domain.mypage.similarartist.converter.SimilarArtistConverter;
import com.umc.banddy.domain.mypage.similarartist.web.dto.ArtistSuggestionQuestionResponse;
import com.umc.banddy.domain.mypage.similarartist.web.dto.SimilarArtistResponse;
import com.umc.banddy.global.util.MemberSimilarityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimilarArtistService {

    private final MemberRepository memberRepository;
    private final MemberArtistRepository memberArtistRepository;
    private final MemberSimilarityUtil similarityUtil;

    public List<SimilarArtistResponse> getArtistsSavedBySimilarUsers(Long loginMemberId) {
        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<Member> similarMembers = similarityUtil.findSimilarMembers(loginMember);

        similarMembers = similarMembers.stream()
                .filter(m -> !m.getId().equals(loginMemberId))
                .distinct()
                .toList();

        if (similarMembers.isEmpty()) {
            return List.of();
        }

        List<Artist> topArtists = memberArtistRepository
                .findTopSavedArtistsByMembers(similarMembers, loginMemberId, PageRequest.of(0, 5));

        Set<Long> myArtistIds = new HashSet<>(memberArtistRepository.findArtistIdsSavedByMember(loginMemberId));
        List<Artist> filtered = topArtists.stream()
                .filter(a -> !myArtistIds.contains(a.getId()))
                .toList();

        return filtered.stream()
                .map(SimilarArtistConverter::toResponse)
                .collect(Collectors.toList());
    }

    // 한 줄 질문
    public ArtistSuggestionQuestionResponse getArtistSearchHintQuestion(Long loginMemberId) {
        Member me = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<SimilarArtistResponse> list = getArtistsSavedBySimilarUsers(loginMemberId);

        String artistName = list.isEmpty() ? null : list.get(0).getName();
        String question = (artistName != null && !artistName.isBlank())
                ? me.getNickname() + " 님과 취향이 비슷한 사람들이 좋아한 " + artistName + "의 곡은 어때요?"
                : me.getNickname() + " 님이 좋아할 만한 아티스트를 찾아볼까요?";

        return ArtistSuggestionQuestionResponse.builder()
                .question(question)
                .artistName(artistName)
                .memberNickname(me.getNickname())
                .build();
    }
}
