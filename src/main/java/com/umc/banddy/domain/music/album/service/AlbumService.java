package com.umc.banddy.domain.music.album.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.album.converter.AlbumConverter;
import com.umc.banddy.domain.music.album.domain.Album;
import com.umc.banddy.domain.music.album.domain.MemberAlbum;
import com.umc.banddy.domain.music.album.repository.AlbumRepository;
import com.umc.banddy.domain.music.album.repository.AlbumVisibilityResponse;
import com.umc.banddy.domain.music.album.repository.MemberAlbumRepository;
import com.umc.banddy.domain.music.album.web.dto.AlbumRequestDto;
import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import com.umc.banddy.domain.music.album.web.dto.AlbumToggleResponseDto;
import com.umc.banddy.domain.music.folder.domain.FolderAlbums;
import com.umc.banddy.domain.music.folder.repository.FolderAlbumsRepository;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import com.umc.banddy.global.security.oauth.SpotifyTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final FolderAlbumsRepository folderAlbumsRepository;
    private final MemberAlbumRepository memberAlbumRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final SpotifyTokenManager spotifyTokenManager;

    // 앨범 저장
    @Transactional
    public AlbumResponseDto saveAlbum(AlbumRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Album album = albumRepository.findBySpotifyId(requestDto.getSpotifyId())
                .orElseGet(() -> fetchAndSaveAlbumFromSpotify(requestDto.getSpotifyId()));

        MemberAlbum memberAlbum = memberAlbumRepository.findByMemberAndAlbum(member, album)
                .orElseGet(() -> memberAlbumRepository.save(
                        MemberAlbum.builder().member(member).album(album).build()
                ));

        return AlbumConverter.toAlbumResponseDto(album, memberAlbum.getId());
    }

    // 앨범 삭제
    @Transactional
    public void deleteAlbum(Long albumId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));

        // 1. 회원-앨범 매핑 조회
        MemberAlbum memberAlbum = memberAlbumRepository.findByMemberAndAlbum(member, album)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_SAVED_BY_MEMBER));

        // 2. 폴더-앨범 매핑이 있는지 확인 (여러 폴더에 있을 수 있으므로 모두 조회)
        List<FolderAlbums> folderAlbumsList = folderAlbumsRepository.findAllByAlbum(album);

        // 3. 폴더-앨범 매핑이 있으면 모두 삭제
        if (!folderAlbumsList.isEmpty()) {
            folderAlbumsRepository.deleteAll(folderAlbumsList);
        }

        // 4. 회원-앨범 매핑 삭제
        memberAlbumRepository.delete(memberAlbum);
    }


    // 앨범 저장/삭제 토글
    @Transactional
    public AlbumToggleResponseDto toggleAlbum(AlbumRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Album album = albumRepository.findBySpotifyId(requestDto.getSpotifyId())
                .orElseGet(() -> fetchAndSaveAlbumFromSpotify(requestDto.getSpotifyId()));

        var memberAlbumOpt = memberAlbumRepository.findByMemberAndAlbum(member, album);
        boolean isSaved;
        if (memberAlbumOpt.isPresent()) {
            memberAlbumRepository.delete(memberAlbumOpt.get());
            isSaved = false;
        } else {
            memberAlbumRepository.save(
                    MemberAlbum.builder().member(member).album(album).build()
            );
            isSaved = true;
        }

        return AlbumConverter.toAlbumToggleResponseDto(album, isSaved);
    }

    // 저장한 앨범 목록 조회
    @Transactional(readOnly = true)
    public List<AlbumResponseDto> getSavedAlbums(String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return memberAlbumRepository.findAllByMember(member).stream()
                .map(ma -> AlbumConverter.toAlbumResponseDto(ma.getAlbum(), ma.getId()))
                .collect(Collectors.toList());
    }

    // 특정 앨범 상세 조회
    @Transactional(readOnly = true)
    public AlbumResponseDto getAlbumDetail(Long albumId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));
        var memberAlbumOpt = memberAlbumRepository.findByMemberAndAlbum(member, album);
        Long memberAlbumId = memberAlbumOpt.map(MemberAlbum::getId).orElse(null);
        return AlbumConverter.toAlbumResponseDto(album, memberAlbumId);
    }

    // Spotify API에서 앨범 정보 fetch & 저장
    private Album fetchAndSaveAlbumFromSpotify(String spotifyId) {
        try {
            SpotifyApi spotifyApi = spotifyTokenManager.getSpotifyApi();
            se.michaelthelin.spotify.model_objects.specification.Album spotifyAlbum =
                    spotifyApi.getAlbum(spotifyId).build().execute();

            String name = spotifyAlbum.getName();
            String artistNames = (spotifyAlbum.getArtists() != null && spotifyAlbum.getArtists().length > 0)
                    ? spotifyAlbum.getArtists()[0].getName()
                    : "";
            String imageUrl = (spotifyAlbum.getImages() != null && spotifyAlbum.getImages().length > 0)
                    ? spotifyAlbum.getImages()[0].getUrl()
                    : "";
            String externalUrl = (spotifyAlbum.getExternalUrls() != null && spotifyAlbum.getExternalUrls().get("spotify") != null)
                    ? spotifyAlbum.getExternalUrls().get("spotify")
                    : "";

            return albumRepository.save(
                    Album.builder()
                            .spotifyId(spotifyId)
                            .name(name)
                            .artist(artistNames)
                            .imageUrl(imageUrl)
                            .externalUrl(externalUrl)
                            .build()
            );
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SPOTIFY_RESOURCE_NOT_FOUND);
        }
    }

    // 앨범 잠금 상태 수정
    @Transactional
    public AlbumVisibilityResponse updateAlbumVisibility(Long albumId, Boolean isPrivate, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));

        MemberAlbum memberAlbum = memberAlbumRepository.findByMemberAndAlbum(member, album)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_SAVED_BY_MEMBER));

        memberAlbum.setIsPrivate(isPrivate); // 잠금 상태 변경

        return AlbumVisibilityResponse.builder()
                .memberId(member.getId())
                .isPrivate(isPrivate)
                .build();
    }

    // 상대방 공개앨범 조회
    @Transactional(readOnly = true)
    public List<AlbumResponseDto> getSavedAlbumsByOtherMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        return memberAlbumRepository.findAllByMemberAndIsPrivateFalse(member).stream()
                .map(ma -> AlbumConverter.toAlbumResponseDto(ma.getAlbum(), ma.getId()))
                .collect(Collectors.toList());
    }



}
