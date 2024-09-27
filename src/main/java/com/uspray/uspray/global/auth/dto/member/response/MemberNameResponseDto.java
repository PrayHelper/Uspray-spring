package com.uspray.uspray.DTO.auth.response;

import lombok.Getter;

@Getter
public class MemberNameResponseDto {

    private String name;

    public MemberNameResponseDto(String name) {
        this.name = name;
    }
}
