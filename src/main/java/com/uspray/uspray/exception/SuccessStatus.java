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
    CHECK_USER_ID_SUCCESS(HttpStatus.OK, "사용 가능한 아이디입니다."),

    /*
     * 201 created
     */
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),

    /*
     * 204 deleted
     */
    WITHDRAWAL_SUCCESS(HttpStatus.NO_CONTENT, "회원 탈퇴에 성공했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
