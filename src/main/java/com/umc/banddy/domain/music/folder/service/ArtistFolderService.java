package com.umc.banddy.domain.music.folder.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.artist.repository.ArtistRepository;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.domain.music.artist.web.dto.ArtistResponseDto;
import com.umc.banddy.domain.music.folder.converter.ArtistFolderConverter;
import com.umc.banddy.domain.music.folder.domain.ArtistFolder;
import com.umc.banddy.domain.music.folder.domain.FolderArtists;
import com.umc.banddy.domain.music.folder.repository.ArtistFolderRepository;
import com.umc.banddy.domain.music.folder.repository.FolderArtistsRepository;
import com.umc.banddy.domain.music.folder.web.dto.FolderArtistsRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderArtistsResponseDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.exception.handler.FolderHandler;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.mail.Folder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistFolderService {

    private final ArtistFolderRepository artistFolderRepository;
    private final FolderArtistsRepository folderArtistsRepository;
    private final MemberArtistRepository memberArtistRepository;
    private final MemberRepository memberRepository;
    private final ArtistRepository artistRepository;
    private final JwtTokenUtil jwtTokenUtil;

    private static final Set<String> ALLOWED_FOLDER_COLORS = Set.of(
            "GRAY", "YELLOW", "GREEN", "RED", "ORANGE", "BLUE"
    );

    // 폴더 생성
    @Transactional
    public FolderResponseDto createFolder(FolderRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String color = requestDto.getColor();
        if (color == null || !ALLOWED_FOLDER_COLORS.contains(color)) {
            throw new FolderHandler(ErrorStatus.FOLDER_INVALID_COLOR);
        }

        ArtistFolder folder = ArtistFolderConverter.toArtistFolder(requestDto, member);
        ArtistFolder saved = artistFolderRepository.save(folder);
        return ArtistFolderConverter.toFolderResponseDto(saved);
    }

    // 폴더 삭제
    @Transactional
    public void deleteFolder(Long folderId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        ArtistFolder folder = artistFolderRepository.findById(folderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLDER_NOT_FOUND));
        artistFolderRepository.delete(folder);
    }

    // 폴더 목록 조회
    @Transactional(readOnly = true)
    public List<FolderResponseDto> getFoldersByMember(String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return artistFolderRepository.findAllByMember(member).stream()
                .map(ArtistFolderConverter::toFolderResponseDto)
                .collect(Collectors.toList());
    }

    // 폴더에 아티스트 추가 (artistId 사용)
    @Transactional
    public FolderArtistsResponseDto addArtistToFolder(Long folderId, FolderArtistsRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        ArtistFolder folder = artistFolderRepository.findById(folderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLDER_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Artist artist = artistRepository.findById(requestDto.getArtistId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTIST_NOT_FOUND));

        // 회원-아티스트 매핑이 있는지 확인 (내가 저장한 아티스트만 추가 가능)
        MemberArtist memberArtist = memberArtistRepository.findByMemberAndArtist(member, artist)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTIST_NOT_SAVED_BY_MEMBER));

        // 이미 추가된 아티스트면 중복 추가 방지
        folderArtistsRepository.findByArtistFolderAndMemberArtist(folder, memberArtist)
                .orElseGet(() -> folderArtistsRepository.save(
                        FolderArtists.builder().artistFolder(folder).memberArtist(memberArtist).build()
                ));

        // 폴더 내 모든 아티스트 응답
        List<ArtistResponseDto> artistDtos = folderArtistsRepository.findAllByArtistFolder(folder).stream()
                .map(fa -> {
                    MemberArtist ma = fa.getMemberArtist();
                    return ArtistResponseDto.builder()
                            .artistId(ma.getArtist().getId())
                            .spotifyId(ma.getArtist().getSpotifyId())
                            .name(ma.getArtist().getName())
                            .genre(ma.getArtist().getGenre())
                            .imageUrl(ma.getArtist().getImageUrl())
                            .externalUrl(ma.getArtist().getExternalUrl())
                            .build();
                })
                .collect(Collectors.toList());

        return FolderArtistsResponseDto.builder()
                .artistFolderId(folder.getId())
                .artists(artistDtos)
                .build();
    }

    // 폴더 내 아티스트 삭제 (artistId 사용)
    @Transactional
    public void removeArtistFromFolder(Long folderId, Long artistId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        ArtistFolder folder = artistFolderRepository.findById(folderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLDER_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTIST_NOT_FOUND));

        MemberArtist memberArtist = memberArtistRepository.findByMemberAndArtist(member, artist)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTIST_NOT_SAVED_BY_MEMBER));

        folderArtistsRepository.deleteByArtistFolderAndMemberArtist(folder, memberArtist);
    }

    // 특정 폴더 내 아티스트 목록 조회
    @Transactional(readOnly = true)
    public List<ArtistResponseDto> getArtistsInFolder(Long folderId, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        ArtistFolder folder = artistFolderRepository.findById(folderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLDER_NOT_FOUND));

        return folderArtistsRepository.findAllByArtistFolder(folder).stream()
                .map(fa -> {
                    MemberArtist ma = fa.getMemberArtist();
                    return ArtistResponseDto.builder()
                            .artistId(ma.getArtist().getId())
                            .spotifyId(ma.getArtist().getSpotifyId())
                            .name(ma.getArtist().getName())
                            .genre(ma.getArtist().getGenre())
                            .imageUrl(ma.getArtist().getImageUrl())
                            .externalUrl(ma.getArtist().getExternalUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 폴더 수정
    @Transactional
    public FolderResponseDto updateFolder(Long folderId, FolderRequestDto requestDto, String token) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        ArtistFolder folder = artistFolderRepository.findById(folderId)
                .orElseThrow(() -> new FolderHandler(ErrorStatus.FOLDER_NOT_FOUND));

        // 권한 체크
        if (!folder.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 이름 수정
        if (requestDto.getName() != null) {
            folder.setName(requestDto.getName());
        }

        // 색상 수정 및 유효성 검사
        if (requestDto.getColor() != null) {
            if (!ALLOWED_FOLDER_COLORS.contains(requestDto.getColor())) {
                throw new FolderHandler(ErrorStatus.FOLDER_INVALID_COLOR);
            }
            folder.setColor(requestDto.getColor());
        }

        ArtistFolder updated = artistFolderRepository.save(folder);
        return ArtistFolderConverter.toFolderResponseDto(updated);
    }
}
