package com.uspray.uspray.domain.member.controller;

import com.uspray.uspray.domain.member.dto.request.CheckPwDTO;
import com.uspray.uspray.domain.member.dto.request.FcmTokenDto;
import com.uspray.uspray.domain.member.dto.request.NotificationAgreeDto;
import com.uspray.uspray.domain.member.dto.request.OauthNameDto;
import com.uspray.uspray.domain.member.dto.response.NotificationInfoDto;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.userdetails.User;

@SecurityRequirement(name = "JWT Auth")
@Tag(name = "Member", description = "유저 관련 api")
public interface MemberApi {

    @Operation(summary = "전화번호 변경")
    ApiResponseDto<?> changePhone(
        @Parameter(hidden = true) User user,
        @Schema(example = "01046518879") String changePhone);

    @Operation(summary = "알림 On/Off 조회")
    ApiResponseDto<NotificationInfoDto> getNotificationAgree(
        @Parameter(hidden = true) User user);

    @Operation(summary = "알림 On/Off")
    ApiResponseDto<?> setNotificationAgree(
        @Parameter(hidden = true) User user,
        NotificationAgreeDto notificationAgreeDto);

    @Operation(summary = "소셜 로그인 회원가입 이름 설정")
    ApiResponseDto<?> setOAuthName(OauthNameDto oauthNameDto);

    @Operation(summary = "비밀번호 확인")
    ApiResponseDto<Boolean> checkPw(
        @Parameter(hidden = true) User user,
        CheckPwDTO checkPwDto);

    @Operation(summary = "비밀번호 변경")
    ApiResponseDto<?> changePw(@Parameter(hidden = true) User user,
        CheckPwDTO changePwDto);

    @Operation(summary = "FCM 토큰 업데이트")
    @SecurityRequirement(name = "JWT Auth")
    ApiResponseDto<?> updateFcmToken(
        @Parameter(hidden = true) User user,
        FcmTokenDto fcmTokenDto);
}
