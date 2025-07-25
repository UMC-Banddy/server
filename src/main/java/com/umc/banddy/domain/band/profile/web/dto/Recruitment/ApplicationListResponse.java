package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import com.umc.banddy.domain.band.profile.enums.BandStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ApplicationListResponse {

    private String bandName;
    private String bandImage;
    private List<String> sessions;
    private BandStatus status;
    private List<BandChatSummaryDto> bandChatList;
}
