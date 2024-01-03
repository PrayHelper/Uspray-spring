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
    GET_GROUP_PRAY_LIST_SUCCESS(HttpStatus.OK, "기도제목 목록 조회에 성공했습니다."),
    GET_PRAY_SUCCESS(HttpStatus.OK, "기도제목 조회에 성공했습니다."),
    SEND_SMS_SUCCESS(HttpStatus.OK, "SMS를 성공적으로 전송했습니다."),
    CERTIFICATION_SUCCESS(HttpStatus.OK, "전화번호 인증에 성공했습니다."),
    CHANGE_PHONE_SUCCESS(HttpStatus.OK, "전화번호를 성공적으로 변경했습니다."),
    FIND_USER_ID_SUCCESS(HttpStatus.OK, "아이디를 성공적으로 조회했습니다."),
    CHECK_USER_PW_SUCCESS(HttpStatus.OK, "비밀번호가 일치합니다."),
    FIND_USER_PW_SUCCESS(HttpStatus.OK, "존재하는 사용자입니다."),
    CHANGE_USER_PW_SUCCESS(HttpStatus.OK, "비밀번호를 성공적으로 변경했습니다."),
    CHECK_USER_ID_SUCCESS(HttpStatus.OK, "사용 가능한 아이디입니다."),
    UPDATE_PRAY_SUCCESS(HttpStatus.OK, "기도제목 수정에 성공했습니다."),
    UPDATE_GROUP_PRAY_SUCCESS(HttpStatus.OK, "기도제목 수정에 성공했습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공했습니다."),
    PUSH_SUCCESS(HttpStatus.OK, "푸쉬 알림을 성공적으로 전송했습니다."),
    CHANGE_PUSH_AGREE_SUCCESS(HttpStatus.OK, "푸쉬 알림 설정을 성공적으로 변경했습니다."),
    CHANGE_NAME_SUCCESS(HttpStatus.OK, "이름을 성공적으로 변경했습니다."),
    UPDATE_CATEGORY_SUCCESS(HttpStatus.OK, "카테고리 수정에 성공했습니다."),
    GET_CATEGORY_SUCCESS(HttpStatus.OK, "카테고리 조회에 성공했습니다."),
    GET_HISTORY_LIST_SUCCESS(HttpStatus.OK, "히스토리 목록 조회에 성공했습니다."),
    GET_HISTORY_DETAIL_SUCCESS(HttpStatus.OK, "히스토리 상세 조회에 성공했습니다."),
    INCREASE_PRAY_COUNT_SUCCESS(HttpStatus.OK, "기도 횟수 추가에 성공했습니다."),
    COMPLETE_PRAY_SUCCESS(HttpStatus.OK, "기도제목 완료에 성공했습니다."),
    GET_CATEGORY_LIST_SUCCESS(HttpStatus.OK, "카테고리 목록 조회에 성공했습니다."),
    GET_GROUP_LIST_SUCCESS(HttpStatus.OK, "모임 목록 조회에 성공했습니다."),
    GET_GROUP_DETAIL_SUCCESS(HttpStatus.OK, "모임 상세 조회에 성공했습니다."),
    CHANGE_GROUP_NAME_SUCCESS(HttpStatus.OK, "모임 이름 변경에 성공했습니다."),
    CHANGE_GROUP_LEADER_SUCCESS(HttpStatus.OK, "모임 리더 위임에 성공했습니다."),
    KICK_GROUP_MEMBER_SUCCESS(HttpStatus.OK, "모임 멤버 내보내기에 성공했습니다."),
    ADD_GROUP_MEMBER_SUCCESS(HttpStatus.OK, "모임 멤버 추가하기에 성공했습니다."),
    GET_MEMBER_LIST_SUCCESS(HttpStatus.OK, "모임 멤버 조회에 성공했습니다."),
    CANCEL_PRAY_SUCCESS(HttpStatus.OK, "기도제목 취소에 성공했습니다."),
    PRAY_TO_GROUP_PRAY_SUCCESS(HttpStatus.OK, "모임 기도제목으로 성공적으로 불러왔습니다."),
    LIKE_GROUP_PRAY_SUCCESS(HttpStatus.OK, "모임 기도제목 좋아요를 성공했습니다."),
    SCARP_GROUP_PRAY_SUCCESS(HttpStatus.OK, "모임 기도제목 스크랩을 성공했습니다."),
    TEST_SUCCESS(HttpStatus.OK, "Test :: OK"),

    /*
     * 201 created
     */
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    CREATE_PRAY_SUCCESS(HttpStatus.CREATED, "기도제목 생성에 성공했습니다."),
    CREATE_GROUP_PRAY_SUCCESS(HttpStatus.CREATED, "모임 기도제목 생성에 성공했습니다."),
    CREATE_CATEGORY_SUCCESS(HttpStatus.CREATED, "카테고리 생성에 성공했습니다."),
    SHARE_PRAY_SUCCESS(HttpStatus.CREATED, "기도제목 공유에 성공했습니다."),
    SHARE_PRAY_AGREE_SUCCESS(HttpStatus.CREATED, "기도제목 공유 수락에 성공했습니다."),
    CREATE_GROUP_SUCCESS(HttpStatus.CREATED, "모임 생성에 성공했습니다."),

    /*
     * 204 deleted
     */
    DELETE_PRAY_SUCCESS(HttpStatus.NO_CONTENT, "기도제목 삭제에 성공했습니다."),
    DELETE_GROUP_PRAY_SUCCESS(HttpStatus.NO_CONTENT, "모임 기도제목 삭제에 성공했습니다."),
    WITHDRAWAL_SUCCESS(HttpStatus.NO_CONTENT, "회원 탈퇴에 성공했습니다."),
    DELETE_CATEGORY_SUCCESS(HttpStatus.NO_CONTENT, "카테고리 삭제에 성공했습니다."),
    DELETE_GROUP_SUCCESS(HttpStatus.NO_CONTENT, "모임 삭제에 성공했습니다."),
    LEAVE_GROUP_SUCCESS(HttpStatus.NO_CONTENT, "모임 탈퇴에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
