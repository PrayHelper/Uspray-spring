package com.uspray.uspray.global.external.client.oauth2.apple;

import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
import com.uspray.uspray.global.external.client.oauth2.apple.dto.AppleAuthCodeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppleController {

    private final AppleMemberService appleMemberService;

    @PostMapping("/apple/login")
    public ApiResponseDto<?> appleLogin(@RequestBody String authCode) {
        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS, appleMemberService.login(authCode));
    }

    @PostMapping("/login/oauth2/code/apple")
    public ApiResponseDto<?> appleLoginRe(@RequestBody AppleAuthCodeDto authCode) {
        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS, appleMemberService.login(authCode.getCode()));
    }

}
