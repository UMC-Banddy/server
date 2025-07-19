package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.web.dto.RecruitmentRequest;
import com.umc.banddy.domain.chat.web.dto.RecruitmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitmentServiceImpl {

    public RecruitmentResponse createBand(RecruitmentRequest request) {








        return com.umc.banddy.domain.chat.web.dto.RecruitmentResponse.builder()
                .bandId(1L) // 임시로 1L로 설정, 실제 구현에서는 DB에서 생성된 밴드 ID를 가져와야 함
                .bandName(request.getBandName())
                .bandDescription(request.getBandDescription())
                .build();
    }
}
