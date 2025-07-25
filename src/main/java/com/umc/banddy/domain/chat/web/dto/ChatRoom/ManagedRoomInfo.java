package com.umc.banddy.domain.chat.web.dto.ChatRoom;

import com.umc.banddy.domain.band.profile.domain.mapping.BandSession;
import com.umc.banddy.domain.band.profile.enums.BandStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ManagedRoomInfo {

    private Long bandId;

    private String bandName;

    private String profileImageUrl;

    private BandStatus bandStatus;

    private List<String> recruitingSession;

    private List<InterviewRoomInfo> rooms;
}
