package com.umc.banddy.domain.music.artist.service;

import com.umc.banddy.domain.member.Member;
import com.umc.banddy.domain.member.MemberRepository;
import com.umc.banddy.domain.music.artist.converter.ArtistConverter;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.artist.repository.ArtistRepository;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.domain.music.artist.web.dto.ArtistRequestDto;
import com.umc.banddy.domain.music.artist.web.dto.ArtistResponseDto;
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
            throw new RuntimeException("Spotify에서 아티스트 정보를 가져올 수 없습니다: " + e.getMessage(), e);
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
        memberArtistRepository.deleteByMemberAndArtist(member, artist);
    }

    // 아티스트 저장/삭제 토글
    @Transactional
    public ArtistResponseDto toggleArtist(ArtistRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Artist artist = artistRepository.findBySpotifyId(requestDto.getSpotifyId())
                .orElseGet(() -> artistRepository.save(ArtistConverter.toArtist(requestDto)));

        var memberArtistOpt = memberArtistRepository.findByMemberAndArtist(member, artist);
        if (memberArtistOpt.isPresent()) {
            memberArtistRepository.delete(memberArtistOpt.get());
            return ArtistConverter.toArtistResponseDto(artist, null);
        } else {
            MemberArtist memberArtist = memberArtistRepository.save(
                    MemberArtist.builder().member(member).artist(artist).build()
            );
            return ArtistConverter.toArtistResponseDto(artist, memberArtist.getId());
        }
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
