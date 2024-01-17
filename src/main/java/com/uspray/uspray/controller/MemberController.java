package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.auth.request.CheckPwDTO;
import com.uspray.uspray.DTO.auth.request.OauthNameDto;
import com.uspray.uspray.DTO.notification.NotificationAgreeDto;
import com.uspray.uspray.DTO.notification.NotificationInfoDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@SecurityRequirement(name = "JWT Auth")
@Tag(name = "Member", description = "유저 관련 api")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "전화번호 변경")
    @PostMapping("/{changePhone}")
    public ApiResponseDto<?> changePhone(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Schema(example = "01046518879") @PathVariable("changePhone") String changePhone) {
        memberService.changePhone(user.getUsername(), changePhone);
        return ApiResponseDto.success(SuccessStatus.CHANGE_PHONE_SUCCESS);
    }

    @Operation(summary = "알림 On/Off 조회")
    @GetMapping("/notification-setting")
    public ApiResponseDto<NotificationInfoDto> getNotificationAgree(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_NOTIFICATION_AGREE_SUCCESS, memberService.getNotificationAgree(user.getUsername()));
    }

    @Operation(summary = "알림 On/Off")
    @PostMapping("/notification-setting")
    public ApiResponseDto<?> setNotificationAgree(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestBody NotificationAgreeDto notificationAgreeDto) {
        memberService.changeNotificationAgree(user.getUsername(), notificationAgreeDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_PUSH_AGREE_SUCCESS);
    }

    @PutMapping("/oauth")
    @Operation(summary = "소셜 로그인 회원가입 이름 설정")
    public ApiResponseDto<?> setOAuthName(@RequestBody OauthNameDto oauthNameDto) {
        memberService.changeName(oauthNameDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_NAME_SUCCESS);
    }

    @PostMapping("/check-pw")
    @Operation(summary = "비밀번호 확인")
    public ApiResponseDto<Boolean> checkPw(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestBody CheckPwDTO checkPwDto) {
        return ApiResponseDto.success(SuccessStatus.CHECK_USER_PW_SUCCESS, memberService.checkPw(
            user.getUsername(), checkPwDto));
    }

    @PostMapping("/change-pw")
    @Operation(summary = "비밀번호 변경")
    public ApiResponseDto<?> changePw(@Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestBody CheckPwDTO changePwDto) {
        memberService.changePw(user.getUsername(), changePwDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_USER_PW_SUCCESS);
    }
}
