package com.umc.banddy.global.apiPayload.code.status;

import com.umc.banddy.global.apiPayload.code.BaseErrorCode;
import com.umc.banddy.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // Spotify
    SPOTIFY_RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "SPOTIFY4004", "Spotify에서 리소스 정보를 찾을 수 없습니다."),

    // Track
    TRACK_NOT_FOUND(HttpStatus.NOT_FOUND, "TRACK4004", "트랙을 찾을 수 없습니다."),
    TRACK_NOT_SAVED_BY_MEMBER(HttpStatus.BAD_REQUEST, "TRACK4000", "해당 트랙은 회원이 저장한 곡이 아닙니다."),

    // Artist
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTIST4004", "아티스트를 찾을 수 없습니다."),
    //ARTIST_ALREADY_SAVED(HttpStatus.CONFLICT, "ARTIST4009", "이미 저장된 아티스트입니다."),
    ARTIST_NOT_SAVED_BY_MEMBER(HttpStatus.BAD_REQUEST, "ARTIST4000", "해당 아티스트는 회원이 저장한 아티스트가 아닙니다."),

    // Folder
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER4004", "폴더를 찾을 수 없습니다."),
    FOLDER_TRACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER4004", "폴더에 해당 곡이 없습니다."),


    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4004", "회원 정보를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER4002", "이미 존재하는 이메일입니다."),

    // 인증 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4001", "아이디 또는 비밀번호가 일치하지 않습니다."),
    DYNAMIC_KEY_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH4003", "사용자에 대한 동적 키가 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4011", "유효하지 않은 토큰입니다."),
    VERIFICATION_CODE_WRONG(HttpStatus.BAD_REQUEST, "AUTH4021", "인증번호가 일치하지 않습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH4022", "인증번호가 만료되었습니다."),
    LOGOUT_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4002", "이미 로그아웃된 토큰입니다."),
    // Access Token
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_006", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_007", "유효하지 않은 Access Token입니다."),

    // Refresh Token
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_008", "Refresh Token이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_009", "유효하지 않은 Refresh Token입니다.");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
