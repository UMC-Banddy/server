package com.umc.banddy.domain.music.folder.web.dto;

import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderAlbumsResponseDto {
    private Long albumFolderId;
    private List<AlbumResponseDto> albums;
}
