package com.umc.banddy.domain.music.folder.web.dto;

import com.umc.banddy.domain.music.artist.web.dto.ArtistResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderArtistsResponseDto {
    private Long artistFolderId;
    private List<ArtistResponseDto> artists;
}
