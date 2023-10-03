package com.uspray.uspray.Enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DeleteReason {

    DONT_NEED("쓰지 않는 서비스에요."),
    NOT_USABLE("원하는 기능이 없어요"),
    MANY_ERROR("오류가 많아서 쓸 수가 없어요."),
    USE_DIFFICULT("사용하기에 불편함이 있이요."),
    ETC("기타")
    ;
    private final String description;
}
