package com.uspray.uspray.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorStatus {

    /*
     * 400 BAD_REQUEST
     */
    VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    ALREADY_EXIST_ID_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 사용중인 아이디입니다."),
    INVALID_TOKEN_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, "토큰 혹은 만료시간 설정이 잘못되었습니다."),
    SENDER_RECEIVER_SAME_EXCEPTION(HttpStatus.BAD_REQUEST, "자신에게는 기도제목을 공유할 수 없습니다."),
    CATEGORY_ALREADY_EXIST_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 존재하는 카테고리입니다."),
    CATEGORY_LIMIT_EXCEPTION(HttpStatus.BAD_REQUEST, "카테고리는 최대 7개까지 생성 가능합니다."),
    ALREADY_PRAYED_TODAY(HttpStatus.BAD_REQUEST, "오늘 이미 기도한 기도제목입니다."),
    CATEGORY_DUPLICATE_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 존재하는 카테고리입니다."),
    ALREADY_EXIST_GROUP_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 존재하는 모임 회원입니다."),
    LEADER_CANNOT_LEAVE_GROUP_EXCEPTION(HttpStatus.BAD_REQUEST, "모임장은 모임을 떠날 수 없습니다."),
    ALREADY_SHARED_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 공유된 기도제목입니다."),
    ALREADY_EXIST_GROUP_NAME_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 존재하는 모임 이름입니다."),
    ALREADY_CANCEL_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 취소된 기도제목입니다."),
    WRONG_LOGIN_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 틀렸습니다."),
    MISS_MATCH_SMS_CODE(HttpStatus.BAD_REQUEST, "문자 인증 코드가 틀렸습니다."),
    INVALID_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 타입입니다."),

    /*
     * 401 UNAUTHORIZED
     */
    TOKEN_NOT_VALID_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
    REFRESH_TOKEN_NOT_VALID_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않는 리프레시 토큰입니다."),

    /**
     * 403 FORBIDDEN
     */
    PRAY_UNAUTHORIZED_EXCEPTION(HttpStatus.FORBIDDEN, "해당 기도제목에 대한 권한이 없습니다."),
    SHARE_NOT_AUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "기도제목을 공유할 권한이 없습니다."),
    DELETE_NOT_AUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "기도제목을 삭제할 권한이 없습니다."),
    CATEGORY_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 카테고리에 대한 권한이 없습니다."),
    GROUP_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 모임에 대한 권한이 없습니다."),

    /**
     * 404 NOT FOUND
     */
    NOT_FOUND_USER_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다"),
    NOT_FOUND_GROUP_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 모임입니다"),
    NOT_FOUND_GROUP_PRAY_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 모임 기도입니다"),
    PRAY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 기도제목을 찾을 수 없습니다."),
    PRAY_ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND, "원본 기도제목이 삭제되었습니다."),
    NOT_FOUND_SHARED_PRAY_EXCEPTION(HttpStatus.NOT_FOUND, "해당 공유기도제목을 찾을 수 없습니다."),
    HISTORY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 히스토리를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    GROUP_MEMBER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 멤버를 모임에서 찾을 수 없습니다."),
    NOT_FOUND_GROUP_MEMBER_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 모임 회원입니다."),

    /**
     * 500 INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청을 처리할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
