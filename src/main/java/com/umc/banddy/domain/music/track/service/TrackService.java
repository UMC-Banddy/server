package com.umc.banddy.domain.music.track.service;

import com.umc.banddy.domain.music.Member;
import com.umc.banddy.domain.music.MemberRepository;
import com.umc.banddy.domain.music.search.service.MusicSearchService;
import com.umc.banddy.domain.music.search.web.dto.TrackInfo;
import com.umc.banddy.domain.music.track.converter.TrackConverter;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.music.track.repository.TrackRepository;
import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;
import com.umc.banddy.domain.music.track.web.dto.TrackRequestDto;
import com.umc.banddy.domain.music.track.web.dto.TrackToggleResponseDto;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.exception.handler.TrackHandler;
import com.umc.banddy.global.security.oauth.SpotifyTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;



import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final MemberTrackRepository memberTrackRepository;
    private final MemberRepository memberRepository;
    private final MusicSearchService musicSearchService;
    private final SpotifyTokenManager spotifyTokenManager;

    // 하드코딩된 memberId (인증 연동 전 임시)
    private static final Long HARDCODED_MEMBER_ID = 1L;


    /**
     * Spotify API에서 곡 정보를 받아와 저장
     */
    private Track fetchAndSaveTrackFromSpotify(String spotifyId) {
        try {
            SpotifyApi spotifyApi = spotifyTokenManager.getSpotifyApi();
            se.michaelthelin.spotify.model_objects.specification.Track spotifyTrack =
                    spotifyApi.getTrack(spotifyId).build().execute();

            String title = spotifyTrack.getName();
            String artist = (spotifyTrack.getArtists() != null && spotifyTrack.getArtists().length > 0)
                    ? spotifyTrack.getArtists()[0].getName()
                    : "";
            String album = spotifyTrack.getAlbum() != null ? spotifyTrack.getAlbum().getName() : "";
            String duration = null;
            if (spotifyTrack.getDurationMs() != null) {
                int totalSeconds = spotifyTrack.getDurationMs() / 1000;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                duration = String.format("%d:%02d", minutes, seconds);
            }
            String imageUrl = (spotifyTrack.getAlbum() != null && spotifyTrack.getAlbum().getImages() != null
                    && spotifyTrack.getAlbum().getImages().length > 0)
                    ? spotifyTrack.getAlbum().getImages()[0].getUrl()
                    : "";

            return trackRepository.save(
                    TrackConverter.toTrackFromSpotify(
                            spotifyId,
                            title,
                            artist,
                            album,
                            duration,
                            imageUrl
                    )
            );
        } catch (Exception e) {
            throw new TrackHandler(ErrorStatus.SPOTIFY_RESOURCE_NOT_FOUND);
        }
    }



    /**
     * 곡 저장 (spotifyId만 받음, 곡 정보는 Spotify API에서 직접 조회)
     */
    @Transactional
    public TrackResponseDto.TrackResultDto saveTrack(TrackRequestDto.TrackSaveDto requestDto) {
        String spotifyId = requestDto.getSpotifyId().trim();

        // 1. 이미 저장된 곡이 있으면 사용, 없으면 Spotify API에서 조회 후 저장
        Track track = trackRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> fetchAndSaveTrackFromSpotify(spotifyId));

        // 2. 하드코딩된 회원 정보 조회
        Member member = memberRepository.findById(HARDCODED_MEMBER_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 3. 회원-곡 매핑(MemberTrack) 저장 (중복 저장 방지)
        MemberTrack memberTrack = memberTrackRepository.findByMemberAndTrack(member, track)
                .orElseGet(() -> memberTrackRepository.save(
                        MemberTrack.builder().member(member).track(track).build()
                ));

        // 4. 응답 DTO 반환
        return TrackConverter.toTrackResultDto(track, memberTrack.getId());
    }


    /**
     * 곡 삭제 (회원-곡 매핑만 삭제)
     */
    @Transactional
    public void deleteTrack(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_FOUND));
        Member member = memberRepository.findById(HARDCODED_MEMBER_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 회원이 저장한 곡인지 확인
        MemberTrack memberTrack = memberTrackRepository.findByMemberAndTrack(member, track)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_SAVED_BY_MEMBER));

        memberTrackRepository.delete(memberTrack);
    }





    /**
     * 내 저장곡 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TrackResponseDto.TrackResultDto> getAllTracks() {
        Member member = memberRepository.findById(HARDCODED_MEMBER_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return memberTrackRepository.findAllByMember(member).stream()
                .map(mt -> TrackConverter.toTrackResultDto(mt.getTrack(), mt.getId()))
                .collect(Collectors.toList());
    }



    /**
     * 내 저장곡 중 특정 곡 상세 조회 (trackId로 조회)
     */
    @Transactional(readOnly = true)
    public TrackResponseDto.TrackResultDto getTrackByTrackId(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_FOUND));
        Member member = memberRepository.findById(HARDCODED_MEMBER_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        MemberTrack memberTrack = memberTrackRepository.findByMemberAndTrack(member, track)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_SAVED_BY_MEMBER));
        return TrackConverter.toTrackResultDto(track, memberTrack.getId());
    }


    /**
     * 곡 저장/삭제 토글
     */
    @Transactional
    public TrackToggleResponseDto toggleTrack(String spotifyId) {
        // 1. 곡 정보 조회, 없으면 저장
        Track track = trackRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> trackRepository.save(fetchAndSaveTrackFromSpotify(spotifyId)));

        // 2. 하드코딩된 회원 정보 조회
        Member member = memberRepository.findById(HARDCODED_MEMBER_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 3. 회원-곡 매핑(MemberTrack) 저장/삭제
        var saved = memberTrackRepository.findByMemberAndTrack(member, track);
        boolean isSaved;
        if (saved.isPresent()) {
            memberTrackRepository.delete(saved.get());
            isSaved = false;
        } else {
            memberTrackRepository.save(MemberTrack.builder().member(member).track(track).build());
            isSaved = true;
        }
        // 4. 토글 결과 반환
        return TrackToggleResponseDto.builder()
                .trackId(track.getId())
                .spotifyId(track.getSpotifyId())
                .isSaved(isSaved)
                .build();
    }


}