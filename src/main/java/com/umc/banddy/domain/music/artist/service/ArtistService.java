package com.umc.banddy.domain.music.artist.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.artist.converter.ArtistConverter;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.artist.repository.ArtistRepository;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.domain.music.artist.web.dto.ArtistRequestDto;
import com.umc.banddy.domain.music.artist.web.dto.ArtistResponseDto;
import com.umc.banddy.domain.music.artist.web.dto.ArtistToggleResponseDto;
import com.umc.banddy.domain.music.folder.domain.FolderArtists;
import com.umc.banddy.domain.music.folder.repository.FolderArtistsRepository;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
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
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final MemberArtistRepository memberArtistRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final SpotifyTokenManager spotifyTokenManager;
    private final FolderArtistsRepository folderArtistsRepository;

    // 아티스트 저장
    @Transactional
    public ArtistResponseDto saveArtist(ArtistRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 1. 이미 저장된 아티스트가 있으면 사용, 없으면 Spotify API에서 fetch 후 저장
        Artist artist = artistRepository.findBySpotifyId(requestDto.getSpotifyId())
                .orElseGet(() -> fetchAndSaveArtistFromSpotify(requestDto.getSpotifyId()));

        MemberArtist memberArtist = memberArtistRepository.findByMemberAndArtist(member, artist)
                .orElseGet(() -> memberArtistRepository.save(
                        MemberArtist.builder().member(member).artist(artist).build()
                ));

        return ArtistConverter.toArtistResponseDto(artist, memberArtist.getId());
    }

    /**
     * Spotify API에서 아티스트 정보를 받아와 저장
     */
    private Artist fetchAndSaveArtistFromSpotify(String spotifyId) {
        try {
            SpotifyApi spotifyApi = spotifyTokenManager.getSpotifyApi();
            se.michaelthelin.spotify.model_objects.specification.Artist spotifyArtist =
                    spotifyApi.getArtist(spotifyId).build().execute();

            String name = spotifyArtist.getName();
            String genre = (spotifyArtist.getGenres() != null && spotifyArtist.getGenres().length > 0)
                    ? String.join(", ", spotifyArtist.getGenres())
                    : "";
            String imageUrl = (spotifyArtist.getImages() != null && spotifyArtist.getImages().length > 0)
                    ? spotifyArtist.getImages()[0].getUrl()
                    : "";

            return artistRepository.save(
                    Artist.builder()
                            .spotifyId(spotifyId)
                            .name(name)
                            .genre(genre)
                            .imageUrl(imageUrl)
                            .build()
            );
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SPOTIFY_RESOURCE_NOT_FOUND);
        }
    }


    // 아티스트 삭제
    @Transactional
    public void deleteArtist(Long artistId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTIST_NOT_FOUND));

        // 1. 회원-아티스트 매핑 조회
        MemberArtist memberArtist = memberArtistRepository.findByMemberAndArtist(member, artist)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTIST_NOT_SAVED_BY_MEMBER));

        // 2. 폴더-아티스트 매핑이 있는지 확인 (여러 폴더에 있을 수 있으므로 모두 조회)
        List<FolderArtists> folderArtistsList = folderArtistsRepository.findAllByMemberArtist(memberArtist);

        // 3. 폴더-아티스트 매핑이 있으면 모두 삭제
        if (!folderArtistsList.isEmpty()) {
            folderArtistsRepository.deleteAll(folderArtistsList);
        }

        // 4. 회원-아티스트 매핑 삭제
        memberArtistRepository.delete(memberArtist);
    }


    // 아티스트 저장/삭제 토글
    @Transactional
    public ArtistToggleResponseDto toggleArtist(ArtistRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Artist artist = artistRepository.findBySpotifyId(requestDto.getSpotifyId())
                .orElseGet(() -> artistRepository.save(ArtistConverter.toArtist(requestDto)));

        var memberArtistOpt = memberArtistRepository.findByMemberAndArtist(member, artist);
        boolean isSaved;
        if (memberArtistOpt.isPresent()) {
            memberArtistRepository.delete(memberArtistOpt.get());
            isSaved = false;
        } else {
            memberArtistRepository.save(
                    MemberArtist.builder().member(member).artist(artist).build()
            );
            isSaved = true;
        }

        return ArtistToggleResponseDto.builder()
                .artistId(artist.getId())
                .spotifyId(artist.getSpotifyId())
                .isSaved(isSaved)
                .build();
    }


    // 저장한 아티스트 목록 조회
    @Transactional(readOnly = true)
    public List<ArtistResponseDto> getSavedArtists(String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return memberArtistRepository.findAllByMember(member).stream()
                .map(ma -> ArtistConverter.toArtistResponseDto(ma.getArtist(), ma.getId()))
                .collect(Collectors.toList());
    }

    // 특정 아티스트 상세 조회
    @Transactional(readOnly = true)
    public ArtistResponseDto getArtistDetail(Long artistId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTIST_NOT_FOUND));
        var memberArtistOpt = memberArtistRepository.findByMemberAndArtist(member, artist);
        Long memberArtistId = memberArtistOpt.map(MemberArtist::getId).orElse(null);
        return ArtistConverter.toArtistResponseDto(artist, memberArtistId);
    }

}
