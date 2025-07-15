package com.umc.banddy.domain.music.folder.converter;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.folder.domain.AlbumFolder;
import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;

public class AlbumFolderConverter {

    public static AlbumFolder toAlbumFolder(FolderRequestDto dto, Member member) {
        return AlbumFolder.builder()
                .name(dto.getName())
                .member(member)
                .build();
    }

    public static FolderResponseDto toFolderResponseDto(AlbumFolder folder) {
        return FolderResponseDto.builder()
                .folderId(folder.getId())
                .name(folder.getName())
                .build();
    }
}

