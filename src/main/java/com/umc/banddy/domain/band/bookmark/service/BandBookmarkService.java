package com.umc.banddy.domain.band.bookmark.service;

import com.umc.banddy.domain.band.bookmark.converter.BandBookmarkConverter;
import com.umc.banddy.domain.band.bookmark.domain.mapping.BandBookmark;
import com.umc.banddy.domain.band.bookmark.repository.BandBookmarkRepository;
import com.umc.banddy.domain.band.bookmark.web.dto.BandBookmarkResponse;
import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.repository.BandRepository;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BandBookmarkService {

    private final MemberRepository memberRepository;
    private final BandRepository bandRepository;
    private final BandBookmarkRepository bandBookmarkRepository;

    /**
     * 밴드 북마크 저장
     */
    public void save(Long memberId, Long bandId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAND_NOT_FOUND));

        // 이미 저장한 경우 예외 처리
        if (bandBookmarkRepository.findByMemberAndBand(member, band).isPresent()) {
            throw new GeneralException(ErrorStatus.BAND_ALREADY_BOOKMARKED);
        }

        BandBookmark bookmark = BandBookmark.builder()
                .member(member)
                .band(band)
                .build();

        bandBookmarkRepository.save(bookmark);
    }

    /**
     * 저장한 밴드 목록 조회
     */
    public List<BandBookmarkResponse> getBookmarkedBands(Long memberId) {
        List<BandBookmark> bookmarks = bandBookmarkRepository.findByMemberId(memberId);
        return BandBookmarkConverter.toResponseList(bookmarks);
    }

    /**
     * 저장한 밴드 삭제
     */
    public void delete(Long memberId, Long bandId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAND_NOT_FOUND));

        BandBookmark bookmark = bandBookmarkRepository.findByMemberAndBand(member, band)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAND_NOT_BOOKMARKED));

        bandBookmarkRepository.delete(bookmark);
    }
}
