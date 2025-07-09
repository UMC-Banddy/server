package com.umc.banddy.domain.music.folder.converter;

import com.umc.banddy.domain.music.Member;
import com.umc.banddy.domain.music.folder.domain.TrackFolder;
import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;

public class TrackFolderConverter {

    // Request DTO → Entity
    public static TrackFolder toTrackFolder(FolderRequestDto dto, Member member) {
        return TrackFolder.builder()
                .name(dto.getName())
                .member(member)
                .build();
    }

    // Entity → Response DTO
    public static FolderResponseDto toFolderResponseDto(TrackFolder folder) {
        return FolderResponseDto.builder()
                .folderId(folder.getId())
                .name(folder.getName())
                .build();
    }
}
