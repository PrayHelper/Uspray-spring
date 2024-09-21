package com.uspray.uspray.global.external.client.oauth2.apple.dto;

import com.uspray.uspray.DTO.auth.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppleLoginResponseDto {

    @Schema(description = "유저 이름", example = "Uspray")
    private String name;
    @Schema(description = "소셜로그인 id", example = "001519.f469d8b268704fe4b8e4c01972104ee5.1051")
    private String socialId;
    @Schema(description = "Access + Refresh Token")
    private TokenDto tokenDto;

    public static AppleLoginResponseDto of(String name, String socialId, TokenDto tokenDto) {
        return new AppleLoginResponseDto(name, socialId, tokenDto);
    }

}
