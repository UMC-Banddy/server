package com.umc.banddy.domain.other.profile.service;

import com.umc.banddy.domain.other.profile.web.dto.SavedTrackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedTrackService {

    public List<SavedTrackResponse> getSavedTracks(Long memberId) {
        // 임시 mock 데이터 반환
        return List.of(
                SavedTrackResponse.builder()
                        .trackId(91L)
                        .title("노래 제목")
                        .artist("아티스트 이름")
                        .imageUrl("https://cdn.example.com/cover.jpg")
                        .soundUrl("https://cdn.example.com/preview.mp3")
                        .build(),
                SavedTrackResponse.builder()
                        .trackId(92L)
                        .title("다른 노래")
                        .artist("다른 아티스트")
                        .imageUrl("https://cdn.example.com/another.jpg")
                        .soundUrl("https://cdn.example.com/preview2.mp3")
                        .build()
        );
    }
}
