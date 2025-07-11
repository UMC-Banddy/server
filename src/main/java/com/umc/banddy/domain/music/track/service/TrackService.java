package com.umc.banddy.domain.music.track.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.folder.domain.FolderTracks;
import com.umc.banddy.domain.music.folder.repository.FolderTracksRepository;
import com.umc.banddy.domain.music.folder.repository.TrackFolderRepository;
import com.umc.banddy.domain.music.search.service.MusicSearchService;
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
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
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
    private final JwtTokenUtil jwtTokenUtil;
    private final TrackFolderRepository trackFolderRepository;
    private final FolderTracksRepository folderTracksRepository;


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
            String externalUrl = null;
            if (spotifyTrack.getExternalUrls() != null && spotifyTrack.getExternalUrls().get("spotify") != null) {
                externalUrl = spotifyTrack.getExternalUrls().get("spotify");
            }

            return trackRepository.save(
                    TrackConverter.toTrackFromSpotify(
                            spotifyId,
                            title,
                            artist,
                            album,
                            duration,
                            imageUrl,
                            externalUrl
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
    public TrackResponseDto.TrackResultDto saveTrack(TrackRequestDto.TrackSaveDto requestDto, String token) {
        String spotifyId = requestDto.getSpotifyId().trim();

        // 1. 이미 저장된 곡이 있으면 사용, 없으면 Spotify API에서 조회 후 저장
        Track track = trackRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> fetchAndSaveTrackFromSpotify(spotifyId));


        // 2. 토큰에서 memberId 추출
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
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
     * 곡 삭제
     */
    @Transactional
    public void deleteTrack(Long trackId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_FOUND));

        // 1. 회원-곡 매핑 조회
        MemberTrack memberTrack = memberTrackRepository.findByMemberAndTrack(member, track)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_SAVED_BY_MEMBER));

        // 2. 폴더-곡 매핑이 있는지 확인 (여러 폴더에 있을 수 있으므로 모두 조회)
        List<FolderTracks> folderTracksList = folderTracksRepository.findAllByMemberTrack(memberTrack);

        // 3. 폴더-곡 매핑이 있으면 모두 삭제
        if (!folderTracksList.isEmpty()) {
            folderTracksRepository.deleteAll(folderTracksList);
        }

        // 4. 회원-곡 매핑 삭제
        memberTrackRepository.delete(memberTrack);
    }



    /**
     * 내 저장곡 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TrackResponseDto.TrackResultDto> getAllTracks(String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return memberTrackRepository.findAllByMember(member).stream()
                .map(mt -> TrackConverter.toTrackResultDto(mt.getTrack(), mt.getId()))
                .collect(Collectors.toList());
    }


    /**
     * 내 저장곡 중 특정 곡 상세 조회 (trackId로 조회)
     */
    @Transactional(readOnly = true)
    public TrackResponseDto.TrackResultDto getTrackByTrackId(Long trackId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        MemberTrack memberTrack = memberTrackRepository.findByMemberAndTrack(member, track)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_SAVED_BY_MEMBER));
        return TrackConverter.toTrackResultDto(track, memberTrack.getId());
    }


    /**
     * 곡 저장/삭제 토글
     */
    @Transactional
    public TrackToggleResponseDto toggleTrack(String spotifyId, String token) {
        // 1. 곡 정보 조회, 없으면 저장
        Track track = trackRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> trackRepository.save(fetchAndSaveTrackFromSpotify(spotifyId)));

        // 2. 토큰에서 memberId 추출
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
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