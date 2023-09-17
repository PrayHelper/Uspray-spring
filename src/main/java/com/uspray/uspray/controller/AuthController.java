package com.uspray.uspray.controller;

import com.uspray.uspray.common.dto.ApiResponseDto;
import com.uspray.uspray.controller.dto.TokenDto;
import com.uspray.uspray.controller.dto.request.MemberLoginRequestDto;
import com.uspray.uspray.controller.dto.request.MemberRequestDto;
import com.uspray.uspray.controller.dto.response.MemberResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponseDto<MemberResponseDto> signup(@RequestBody @Valid MemberRequestDto memberRequestDto) {
        return ApiResponseDto.success(SuccessStatus.SIGNUP_SUCCESS, authService.signup(memberRequestDto));
    }

    @PostMapping("/login")
    public ApiResponseDto<TokenDto> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS, authService.login(memberLoginRequestDto));
    }

//    @PostMapping("/reissue")
//    public ApiResponseDto<TokenDto> reissue(@RequestBody TokenDto tokenDto) {
//        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS, authService.reissue(tokenDto));
//    }
}
