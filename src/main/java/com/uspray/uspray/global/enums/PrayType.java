package com.uspray.uspray.global.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PrayType {
    SHARED("공유 기도"),
    PERSONAL("개인 기도")
    ;
    private final String title;
    
    public String stringValue() {
        return this.name();
    }
}
