package com.uspray.uspray.Enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CategoryType {
    SHARED("공유 기도"),
    PERSONAL("개인 기도");
    private final String title;

    public String stringValue() {
        return this.name();
    }
}
