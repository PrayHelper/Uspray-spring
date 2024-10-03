package com.uspray.uspray.domain.member.controller;

import com.uspray.uspray.domain.member.dto.request.CheckPwDTO;
import com.uspray.uspray.domain.member.dto.request.FcmTokenDto;
import com.uspray.uspray.domain.member.dto.request.NotificationAgreeDto;
import com.uspray.uspray.domain.member.dto.request.OauthNameDto;
import com.uspray.uspray.domain.member.dto.response.NotificationInfoDto;
import com.uspray.uspray.domain.member.service.MemberService;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController implements MemberApi{

    private final MemberService memberService;

    @PostMapping("/{changePhone}")
    public ApiResponseDto<?> changePhone(
        @AuthenticationPrincipal User user,
        @PathVariable("changePhone") String changePhone) {
        memberService.changePhone(user.getUsername(), changePhone);
        return ApiResponseDto.success(SuccessStatus.CHANGE_PHONE_SUCCESS);
    }

    @GetMapping("/notification-setting")
    public ApiResponseDto<NotificationInfoDto> getNotificationAgree(
        @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_NOTIFICATION_AGREE_SUCCESS,
            memberService.getNotificationAgree(user.getUsername()));
    }

    @PostMapping("/notification-setting")
    public ApiResponseDto<?> setNotificationAgree(
        @AuthenticationPrincipal User user,
        @RequestBody NotificationAgreeDto notificationAgreeDto) {
        memberService.changeNotificationAgree(user.getUsername(), notificationAgreeDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_PUSH_AGREE_SUCCESS);
    }

    @PutMapping("/oauth")
    public ApiResponseDto<?> setOAuthName(@RequestBody OauthNameDto oauthNameDto) {
        memberService.changeName(oauthNameDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_NAME_SUCCESS);
    }

    @PostMapping("/check-pw")
    public ApiResponseDto<Boolean> checkPw(
        @AuthenticationPrincipal User user,
        @RequestBody CheckPwDTO checkPwDto) {
        return ApiResponseDto.success(SuccessStatus.CHECK_USER_PW_SUCCESS, memberService.checkPw(
            user.getUsername(), checkPwDto));
    }

    @PostMapping("/change-pw")
    public ApiResponseDto<?> changePw(@AuthenticationPrincipal User user,
        @RequestBody CheckPwDTO changePwDto) {
        memberService.changePw(user.getUsername(), changePwDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_USER_PW_SUCCESS);
    }


    @PutMapping("/fcm-token")
    public ApiResponseDto<?> updateFcmToken(
        @AuthenticationPrincipal User user,
        @RequestBody FcmTokenDto fcmTokenDto) {
        memberService.updateFcmToken(user.getUsername(), fcmTokenDto);
        return ApiResponseDto.success(SuccessStatus.UPDATE_FCM_TOKEN_SUCCESS);
    }
}
