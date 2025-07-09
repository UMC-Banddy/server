package com.umc.banddy.domain.music.folder.web.dto;
// 폴더에 곡 추가할 때

import lombok.Getter;

@Getter
public class FolderTracksRequestDto {
    //private Long trackId;
    private Long memberTrackId;
}
