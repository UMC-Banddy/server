package com.umc.banddy.domain.member.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.umc.banddy.domain.member.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @Schema(description = "성별", example = "남성")
    @NotNull
    private Gender gender;

    @NotBlank
    private String region;

    @NotBlank
    private String district;

    @NotNull
    private Integer age;
}
