package com.umc.banddy.domain.music.folder.converter;

import com.umc.banddy.domain.music.folder.domain.ArtistFolder;
import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;
import com.umc.banddy.domain.member.domain.Member;

public class ArtistFolderConverter {

    // Request DTO → Entity
    public static ArtistFolder toArtistFolder(FolderRequestDto dto, Member member) {
        return ArtistFolder.builder()
                .name(dto.getName())
                .member(member)
                .build();
    }

    // Entity → Response DTO
    public static FolderResponseDto toFolderResponseDto(ArtistFolder folder) {
        return FolderResponseDto.builder()
                .folderId(folder.getId())
                .name(folder.getName())
                .build();
    }
}




