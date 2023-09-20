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
    FIND_USER_ID_SUCCESS(HttpStatus.OK, "아이디를 성공적으로 조회했습니다."),
    CHANGE_USER_PW_SUCCESS(HttpStatus.OK, "비밀번호를 성공적으로 변경했습니다."),

    /*
     * 201 created
     */
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
