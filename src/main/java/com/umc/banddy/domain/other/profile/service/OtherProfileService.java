package com.umc.banddy.domain.other.profile.service;

import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OtherProfileService {

    public OtherProfileResponse getOtherProfile(Long memberId) {
        // 임시 mock 데이터
        return OtherProfileResponse.builder()
                .memberId(memberId)
                .nickname("나락도 락이다")
                .bio("안녕하세요 JPOP일인생 10년차 나락이입니다. 아직 부족하지만 열정만큼은 누구보다 제일입니다! 믿어만 주시면 어떻게든 연습해...")
                .profileImageUrl("https://cdn.example.com/profile.jpg")
                .age(23)
                .gender("female")
                .region("서울시 노원구")
                .tags(List.of("J-POP", "락", "R&B"))
                .sessions(List.of(
                        new OtherProfileResponse.Session("보컬", "🎤"),
                        new OtherProfileResponse.Session("드럼", "🥁")
                ))
                .favoriteArtists(List.of(
                        new OtherProfileResponse.Artist("Saucy Dog", "https://cdn.example.com/artist1.jpg"),
                        new OtherProfileResponse.Artist("AKASAKI", "https://cdn.example.com/artist2.jpg"),
                        new OtherProfileResponse.Artist("Frederic", "https://cdn.example.com/artist3.jpg")
                ))
                .traits(List.of("지각 안 해요", "미리 조율해요", "핑크 안 나요", "연습 해와요", "주단위 합주 선호"))
                .youtubeUrl("https://youtube.com/@narakrock")
                .instagramUrl("https://instagram.com/narak_rock")
                .isFriend(false)
                .isBlocked(false)
                .canRequestChat(true)
                .build();
    }
}

