package com.umc.banddy.domain.member.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "성별", example = "남성")
public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private final String kor;

    Gender(String kor) {
        this.kor = kor;
    }

    @JsonCreator
    public static Gender from(String input) {
        for (Gender gender : Gender.values()) {
            if (gender.kor.equals(input)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("성별은 '남자' 또는 '여자'만 입력 가능합니다.");
    }
    @JsonValue
    public String toValue() {
        return kor; // Swagger 예시에서도 한글로 출력되게
    }
}
