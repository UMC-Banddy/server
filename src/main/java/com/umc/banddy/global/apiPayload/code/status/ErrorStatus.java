package com.umc.banddy.global.apiPayload.code.status;

import com.umc.banddy.global.apiPayload.code.BaseErrorCode;
import com.umc.banddy.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
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

    // Album
    ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "ALBUM4004", "앨범을 찾을 수 없습니다."),
    ALBUM_NOT_SAVED_BY_MEMBER(HttpStatus.BAD_REQUEST, "ALBUM4000", "해당 앨범은 회원이 저장한 앨범이 아닙니다."),

    // Folder
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER4004", "폴더를 찾을 수 없습니다."),
    FOLDER_TRACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER4004", "폴더에 해당 곡이 없습니다."),
    FOLDER_INVALID_COLOR(HttpStatus.BAD_REQUEST, "FOLDER4000", "유효한 폴더 색상이 아닙니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4004", "회원 정보를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER4002", "이미 존재하는 이메일입니다."),

    // Band
    BAND_NOT_FOUND(HttpStatus.NOT_FOUND, "BAND4004", "해당 밴드를 찾을 수 없습니다."),
    BAND_ALREADY_BOOKMARKED(HttpStatus.CONFLICT, "BAND4009", "이미 저장한 밴드입니다."),
    BAND_NOT_BOOKMARKED(HttpStatus.BAD_REQUEST, "BAND4000", "해당 밴드는 저장한 밴드가 아닙니다."),
    BAND_MANAGER_ONLY_ACTION(HttpStatus.FORBIDDEN, "BAND4001", "이 동작은 해당 밴드의 관리자만 수행할 수 있습니다."),

    // Session
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION4004", "해당 세션을 찾을 수 없습니다 ."),
    BAND_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "BAND_SESSION4004", "해당 밴드 세션을 찾을 수 없습니다."),
    BAND_SESSION_NOT_RECRUITED(HttpStatus.BAD_REQUEST, "BAND_SESSION4000", "해당 세션은 모집 중이 아닙니다."),

    // Genre
    GENRE_NOT_FOUND(HttpStatus.NOT_FOUND, "GENRE4004", "해당 장르를 찾을 수 없습니다."),

    // Chat Room
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4004", "해당 채팅방을 찾을 수 없습니다."),
    CHAT_NOT_PARTICIPATED(HttpStatus.BAD_REQUEST, "CHAT4000", "해당 채팅방에 참여하지 않았습니다."),
    CHAT_ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "CHAT4001", "이미 참여한 채팅방입니다."),
    CHAT_TYPE_NOT_MATCHED(HttpStatus.BAD_REQUEST, "CHAT4002", "채팅방 타입이 일치하지 않습니다."),
    CHAT_CAN_NOT_BE_JOINED(HttpStatus.BAD_REQUEST, "CHAT4003", "해당 채팅방은 참여할 수 없습니다."),
    PRIVATE_CHAT_NEED_RECEIVER(HttpStatus.BAD_REQUEST, "CHAT4006", "개인 채팅은 수신자가 필요합니다."),
    CHATROOM_INVALID_PARTICIPANTS(HttpStatus.BAD_REQUEST, "CHAT4007", "1:1 채팅방 참여자 수가 올바르지 않습니다."),
    CHAT_ALREADY_PINNED(HttpStatus.BAD_REQUEST, "CHAT4008", "이미 고정된 밴드 채팅방입니다."),
    CHAT_ALREADY_UNPINNED(HttpStatus.BAD_REQUEST, "CHAT4009", "이미 고정 해제된 밴드 채팅방입니다."),

    // Notification
    NOTIFICATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "NOTIFICATION4000", "이미 존재하는 알림입니다."),

    // Participant
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4005", "해당 채팅방의 참여자를 찾을 수 없습니다."),
    PARTICIPANT_NOT_FOUND_IN_CHATROOM(HttpStatus.NOT_FOUND, "PARTICIPANT4004", "해당 채팅방의 참여자를 찾을 수 없습니다."),
    PARTICIPANT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PARTICIPANT4000", "이미 존재하는 참여자입니다."),
    PARTICIPANT_NOT_BELONG_TO_CHATROOM(HttpStatus.BAD_REQUEST, "PARTICIPANT4002", "해당 참여자는 채팅방에 속하지 않습니다."),


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

    // 명시적 생성자 추가
    ErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

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
