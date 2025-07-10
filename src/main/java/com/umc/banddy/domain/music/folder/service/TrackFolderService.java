package com.umc.banddy.domain.music.folder.service;

import com.umc.banddy.domain.music.Member;
import com.umc.banddy.domain.music.MemberRepository;
import com.umc.banddy.domain.music.folder.converter.FolderTracksConverter;
import com.umc.banddy.domain.music.folder.converter.TrackFolderConverter;
import com.umc.banddy.domain.music.folder.domain.FolderTracks;
import com.umc.banddy.domain.music.folder.domain.TrackFolder;
import com.umc.banddy.domain.music.folder.repository.FolderTracksRepository;
import com.umc.banddy.domain.music.folder.repository.TrackFolderRepository;
import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderTracksRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderTracksResponseDto;
import com.umc.banddy.domain.music.track.converter.TrackConverter;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.music.track.repository.TrackRepository;
import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.exception.handler.FolderHandler;
import com.umc.banddy.global.apiPayload.exception.handler.TrackHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackFolderService {

    private final TrackFolderRepository trackFolderRepository;
    private final FolderTracksRepository folderTracksRepository;
    private final TrackRepository trackRepository;
    private final MemberTrackRepository memberTrackRepository;

    // 하드코딩된 memberId (인증 연동 전 임시)
    private static final Long HARDCODED_MEMBER_ID = 1L;
    private final MemberRepository memberRepository;


    // 폴더 생성
    @Transactional
    public FolderResponseDto createFolder(FolderRequestDto requestDto) {
        Member member = memberRepository.findById(HARDCODED_MEMBER_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        TrackFolder folder = TrackFolderConverter.toTrackFolder(requestDto, member);
        TrackFolder saved = trackFolderRepository.save(folder);
        return TrackFolderConverter.toFolderResponseDto(saved);
    }


    // 폴더 삭제
    @Transactional
    public void deleteFolder(Long folderId) {
        TrackFolder folder = trackFolderRepository.findById(folderId)
                .orElseThrow(() -> new FolderHandler(ErrorStatus.FOLDER_NOT_FOUND));
        trackFolderRepository.delete(folder);
    }


    // 폴더에 곡 추가
    @Transactional
    public FolderTracksResponseDto addTrackToFolder(Long folderId, FolderTracksRequestDto requestDto) {
        TrackFolder folder = trackFolderRepository.findById(folderId)
                .orElseThrow(() -> new FolderHandler(ErrorStatus.FOLDER_NOT_FOUND));
        MemberTrack memberTrack = memberTrackRepository.findById(requestDto.getMemberTrackId())
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_SAVED_BY_MEMBER));

        // 이미 추가된 곡이면 중복 추가 방지
        FolderTracks folderTracks = folderTracksRepository.findByTrackFolderAndMemberTrack(folder, memberTrack)
                .orElseGet(() -> folderTracksRepository.save(
                        FolderTracksConverter.toFolderTracks(folder, memberTrack)
                ));

        // 이번에 추가된 곡만 리스트로 응답
        List<TrackResponseDto.TrackResultDto> trackDtos = List.of(
                TrackConverter.toTrackResultDto(memberTrack.getTrack(), memberTrack.getId())
        );

        return FolderTracksResponseDto.builder()
                .folderTracksId(folderTracks.getId())
                .trackFolderId(folder.getId())
                .tracks(trackDtos)
                .build();
    }


    // 폴더에서 곡 삭제
    @Transactional
    public void removeTrackFromFolder(Long folderId, Long memberTrackId) {
        TrackFolder folder = trackFolderRepository.findById(folderId)
                .orElseThrow(() -> new FolderHandler(ErrorStatus.FOLDER_NOT_FOUND));
        MemberTrack memberTrack = memberTrackRepository.findById(memberTrackId)
                .orElseThrow(() -> new TrackHandler(ErrorStatus.TRACK_NOT_SAVED_BY_MEMBER));
        folderTracksRepository.deleteByTrackFolderAndMemberTrack(folder, memberTrack);
    }


    // 폴더 내 곡 목록 조회
    @Transactional(readOnly = true)
    public List<TrackResponseDto.TrackResultDto> getTracksInFolder(Long folderId) {
        TrackFolder folder = trackFolderRepository.findById(folderId)
                .orElseThrow(() -> new FolderHandler(ErrorStatus.FOLDER_NOT_FOUND));
        return folderTracksRepository.findAllByTrackFolder(folder).stream()
                .map(folderTracks -> {
                    MemberTrack memberTrack = folderTracks.getMemberTrack();
                    return TrackConverter.toTrackResultDto(memberTrack.getTrack(), memberTrack.getId());
                })
                .collect(Collectors.toList());
    }


    // 폴더 목록 조회
    @Transactional(readOnly = true)
    public List<FolderResponseDto> getFoldersByMember() {
        Member member = memberRepository.findById(HARDCODED_MEMBER_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return trackFolderRepository.findAllByMember(member).stream()
                .map(TrackFolderConverter::toFolderResponseDto)
                .collect(Collectors.toList());
    }

}
