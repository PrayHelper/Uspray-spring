package com.uspray.uspray.Enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotificationType {
    PRAY_TIME("기도 시간: 오전 8시"),
    PRAY_FOR_ME("다른 사람이 내 기도 제목을 기도 했을 때"),
    SHARED_MY_PRAY("다른 사람이 내 기도 제목을 공유 받았을 때")
    ;
    private final String message;
}
