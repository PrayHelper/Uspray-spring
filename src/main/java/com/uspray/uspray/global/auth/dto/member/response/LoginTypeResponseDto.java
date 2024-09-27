package com.uspray.uspray.DTO.auth.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginTypeResponseDto {

    private final boolean isSocial;

    @Builder
    public LoginTypeResponseDto(boolean isSocial) {
        this.isSocial = isSocial;
    }
}
