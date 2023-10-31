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

  /*
   * 401 UNAUTHORIZED
   */
  PRAY_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 기도제목에 대한 권한이 없습니다."),
  TOKEN_NOT_VALID_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
  SHARE_NOT_AUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "기도제목을 공유할 권한이 없습니다."),
  DELETE_NOT_AUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "기도제목을 삭제할 권한이 없습니다."),
  CATEGORY_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 카테고리에 대한 권한이 없습니다."),

  /**
   * 404 NOT FOUND
   */
  NOT_FOUND_USER_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다"),
  PRAY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 기도제목을 찾을 수 없습니다."),
  PRAY_ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND, "원본 기도제목이 삭제되었습니다."),
  NOT_FOUND_SHARED_PRAY_EXCEPTION(HttpStatus.NOT_FOUND, "해당 공유기도제목을 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  public int getHttpStatusCode() {
    return httpStatus.value();
  }
}
