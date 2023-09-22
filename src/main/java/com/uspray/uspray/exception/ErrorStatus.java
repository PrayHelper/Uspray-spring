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
  /*
   * 401 UNAUTHORIZED
   */
  PRAY_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 기도제목에 대한 권한이 없습니다."),

  /*
   * 404 NOT_FOUND
   */
  PRAY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 기도제목을 찾을 수 없습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;

  public int getHttpStatusCode() {
    return httpStatus.value();
  }
}
