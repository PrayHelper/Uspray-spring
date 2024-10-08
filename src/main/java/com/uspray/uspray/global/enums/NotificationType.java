package com.uspray.uspray.global.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotificationType {
    PRAY_TIME("기도합시다~", "오전 8시 기도 할 시간입니다."),
    PRAY_FOR_ME("기도받았다~", "다른 사람이 내 기도 제목을 기도 했을 때"),
    SHARED_MY_PRAY("공유~", "다른 사람이 내 기도 제목을 공유 받았을 때"),
    GROUP_SETTING("그룹 설정~", "그룹 설정이 변경되었을 때"),
    ;
    private final String title;
    private final String body;
}
