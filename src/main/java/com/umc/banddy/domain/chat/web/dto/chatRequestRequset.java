package com.umc.banddy.domain.chat.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class chatRequestRequset {
    private Long targetMemeberId;
    private String message;
}
