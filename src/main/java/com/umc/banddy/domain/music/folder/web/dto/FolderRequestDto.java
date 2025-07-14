package com.umc.banddy.domain.music.folder.web.dto;
// 폴더를 생성할 때
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FolderRequestDto {
    private String name;
}
