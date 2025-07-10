package com.umc.banddy.domain.music.folder.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponseDto {
    //private Long trackFolderId;
    private Long folderId;
    private String name;
}
