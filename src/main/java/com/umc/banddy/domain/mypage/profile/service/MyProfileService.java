package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyProfileService {

    // 내 프로필 조회
    public MyProfileResponse getMyProfile(Long memberId) {
        return new MyProfileResponse(
                memberId,
                "BECK",
                "https://example.com/profile.jpg",
                "나는 파리의 택시운전사",
                List.of("J-POP", "J-Rock", "보컬화이트", "편트렌드팝", "바이브릭", "뉴본"),
                List.of(
                        new MyProfileResponse.SavedTrack("최근 저장곡1", "https://example.com/song1.jpg"),
                        new MyProfileResponse.SavedTrack("최근 저장곡2", "https://example.com/song2.jpg"),
                        new MyProfileResponse.SavedTrack("최근 저장곡3", "https://example.com/song3.jpg")
                ),
                true
        );
    }

    // 내 프로필 수정
    public void updateMyProfile(Long memberId, MyProfileUpdateRequest request) {
        // TODO: 추후 memberId 기반으로 Member 엔티티 불러온 뒤, 필드 업데이트 및 저장
        System.out.println("수정 요청 들어옴: memberId = " + memberId);
        System.out.println("닉네임: " + request.getNickname());
        System.out.println("나이: " + request.getAge());
        System.out.println("지역: " + request.getRegion().getCity() + " " + request.getRegion().getDistrict());
        System.out.println("장르: " + request.getGenres());
        System.out.println("바이오: " + request.getBio());
    }
}
