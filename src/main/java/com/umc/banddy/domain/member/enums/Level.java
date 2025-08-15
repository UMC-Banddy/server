package com.umc.banddy.domain.member.enums;

import lombok.Getter;

@Getter
public enum Level {
    BEGINNER("초보"),
    INTERMEDIATE("중수"),
    ADVANCED("고수");

    private final String label;

    Level(String label) {
        this.label = label;
    }

    public static Level fromLabel(String label) {
        for (Level level : values()) {
            if (level.label.equals(label)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown level label: " + label);
    }
}
