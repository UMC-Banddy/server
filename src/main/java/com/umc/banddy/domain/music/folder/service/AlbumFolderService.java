package com.umc.banddy.domain.music.folder.service;

import com.umc.banddy.domain.member.Member;
import com.umc.banddy.domain.member.MemberRepository;
import com.umc.banddy.domain.music.album.domain.Album;
import com.umc.banddy.domain.music.album.repository.AlbumRepository;
import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import com.umc.banddy.domain.music.folder.converter.AlbumFolderConverter;
import com.umc.banddy.domain.music.folder.domain.AlbumFolder;
import com.umc.banddy.domain.music.folder.domain.FolderAlbums;
import com.umc.banddy.domain.music.folder.repository.AlbumFolderRepository;
import com.umc.banddy.domain.music.folder.repository.FolderAlbumsRepository;
import com.umc.banddy.domain.music.folder.web.dto.FolderAlbumsRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderAlbumsResponseDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumFolderService {

    private final AlbumFolderRepository albumFolderRepository;
    private final FolderAlbumsRepository folderAlbumsRepository;
    private final AlbumRepository albumRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;

    // 폴더 생성
    @Transactional
    public FolderResponseDto createFolder(FolderRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        AlbumFolder folder = AlbumFolderConverter.toAlbumFolder(requestDto, member);
        AlbumFolder saved = albumFolderRepository.save(folder);
        return AlbumFolderConverter.toFolderResponseDto(saved);
    }

    // 폴더 삭제
    @Transactional
    public void deleteFolder(Long folderId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        AlbumFolder folder = albumFolderRepository.findById(folderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLDER_NOT_FOUND));
        if (!folder.getMember().getId().equals(memberId)) {
            throw new RuntimeException("권한 없음");
        }
        albumFolderRepository.delete(folder);
    }

    // 폴더 목록 조회
    @Transactional(readOnly = true)
    public List<FolderResponseDto> getFoldersByMember(String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return albumFolderRepository.findAllByMember(member).stream()
                .map(AlbumFolderConverter::toFolderResponseDto)
                .collect(Collectors.toList());
    }

    // 폴더에 앨범 추가
    @Transactional
    public FolderAlbumsResponseDto addAlbumToFolder(Long folderId, FolderAlbumsRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        AlbumFolder folder = albumFolderRepository.findById(folderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLDER_NOT_FOUND));
        Album album = albumRepository.findById(requestDto.getAlbumId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));

        // 이미 추가된 앨범이면 중복 추가 방지
        folderAlbumsRepository.findByAlbumFolderAndAlbum(folder, album)
                .orElseGet(() -> folderAlbumsRepository.save(
                        FolderAlbums.builder().albumFolder(folder).album(album).build()
                ));

        // 폴더 내 모든 앨범 응답
        List<AlbumResponseDto> albumDtos = folderAlbumsRepository.findAllByAlbumFolder(folder).stream()
                .map(fa -> AlbumResponseDto.builder()
                        .albumId(fa.getAlbum().getId())
                        .spotifyId(fa.getAlbum().getSpotifyId())
                        .name(fa.getAlbum().getName())
                        .artist(fa.getAlbum().getArtist())
                        .imageUrl(fa.getAlbum().getImageUrl())
                        .externalUrl(fa.getAlbum().getExternalUrl())
                        .build())
                .collect(Collectors.toList());

        return FolderAlbumsResponseDto.builder()
                //.folderAlbumsId(null)
                .albumFolderId(folder.getId())
                .albums(albumDtos)
                .build();
    }

    // 폴더 내 앨범 삭제
    @Transactional
    public void removeAlbumFromFolder(Long folderId, Long albumId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        AlbumFolder folder = albumFolderRepository.findById(folderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLDER_NOT_FOUND));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));
        folderAlbumsRepository.deleteByAlbumFolderAndAlbum(folder, album);
    }

    // 특정 폴더 내 앨범 목록 조회
    @Transactional(readOnly = true)
    public List<AlbumResponseDto> getAlbumsInFolder(Long folderId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        AlbumFolder folder = albumFolderRepository.findById(folderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLDER_NOT_FOUND));
        if (!folder.getMember().getId().equals(memberId)) {
            throw new RuntimeException("권한 없음");
        }
        return folderAlbumsRepository.findAllByAlbumFolder(folder).stream()
                .map(fa -> AlbumResponseDto.builder()
                        .albumId(fa.getAlbum().getId())
                        .spotifyId(fa.getAlbum().getSpotifyId())
                        .name(fa.getAlbum().getName())
                        .artist(fa.getAlbum().getArtist())
                        .imageUrl(fa.getAlbum().getImageUrl())
                        .externalUrl(fa.getAlbum().getExternalUrl())
                        .build())
                .collect(Collectors.toList());
    }
}

