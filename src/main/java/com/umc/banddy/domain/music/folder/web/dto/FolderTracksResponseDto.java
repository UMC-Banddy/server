package com.umc.banddy.domain.music.folder.web.dto;

import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderTracksResponseDto {
    private Long folderTracksId;
    private Long trackFolderId;
    private List<TrackResponseDto.TrackResultDto> tracks; // 이번에 추가된 곡(들) 정보 리스트
}

