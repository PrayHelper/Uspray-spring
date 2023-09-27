package com.uspray.uspray.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessStatus {

    /**
     * 200 OK
     */
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    GET_PRAY_LIST_SUCCESS(HttpStatus.OK, "기도제목 목록 조회에 성공했습니다."),
    GET_PRAY_SUCCESS(HttpStatus.OK, "기도제목 조회에 성공했습니다."),
    SEND_SMS_SUCCESS(HttpStatus.OK, "SMS를 성공적으로 전송했습니다."),
    CERTIFICATION_SUCCESS(HttpStatus.OK, "전화번호 인증에 성공했습니다."),
    CHANGE_PHONE_SUCCESS(HttpStatus.OK, "전화번호를 성공적으로 변경했습니다."),
    FIND_USER_ID_SUCCESS(HttpStatus.OK, "아이디를 성공적으로 조회했습니다."),
    CHANGE_USER_PW_SUCCESS(HttpStatus.OK, "비밀번호를 성공적으로 변경했습니다."),
    CHECK_USER_ID_SUCCESS(HttpStatus.OK, "사용 가능한 아이디입니다."),

    /*
     * 201 created
     */
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    CREATE_PRAY_SUCCESS(HttpStatus.CREATED, "기도제목 생성에 성공했습니다."),

    /*
     * 204 deleted
     */
    DELETE_PRAY_SUCCESS(HttpStatus.NO_CONTENT, "기도제목 삭제에 성공했습니다."),
    WITHDRAWAL_SUCCESS(HttpStatus.NO_CONTENT, "회원 탈퇴에 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
