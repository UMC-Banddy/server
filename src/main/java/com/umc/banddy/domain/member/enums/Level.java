package com.umc.banddy.domain.member.enums;

public enum Level {
    BEGINNER("초보"),
    INTERMEDIATE("중수"),
    ADVANCED("고수");

    private final String label;

    Level(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

