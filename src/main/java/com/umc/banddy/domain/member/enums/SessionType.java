package com.umc.banddy.domain.member.enums;

import lombok.Getter;

@Getter
public enum SessionType {

    VOCAL("보컬", "mic"),
    ELECTRIC_GUITAR("일렉 기타", "electric_guitar"),
    ACOUSTIC_GUITAR("어쿠스틱 기타", "acoustic_guitar"),
    BASS("베이스", "bass"),
    DRUM("드럼", "drum"),
    KEYBOARD("키보드", "keyboard"),
    VIOLIN("바이올린", "violin"),
    TRUMPET("트럼펫", "trumpet");

    private final String name;
    private final String icon;

    SessionType(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }
}
