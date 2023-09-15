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

    /*
     * 201 created
     */
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    CREATE_PRAY_SUCCESS(HttpStatus.CREATED, "기도제목 생성에 성공했습니다."),

    /*
     * 204 No Content
     */
    DELETE_PRAY_SUCCESS(HttpStatus.NO_CONTENT, "기도제목 삭제에 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
