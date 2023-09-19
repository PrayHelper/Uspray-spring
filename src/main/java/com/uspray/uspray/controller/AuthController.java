package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.auth.request.MemberLoginRequestDto;
import com.uspray.uspray.DTO.auth.request.MemberRequestDto;
import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.auth.TokenDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uspray.uspray.DTO.auth.response.MemberResponseDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ApiResponse(
            responseCode = "201",
            description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = MemberResponseDto.class)))
    public ApiResponseDto<MemberResponseDto> signup(@RequestBody @Valid MemberRequestDto memberRequestDto) {
        return ApiResponseDto.success(SuccessStatus.SIGNUP_SUCCESS, authService.signup(memberRequestDto));
    }

    @PostMapping("/login")
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = MemberResponseDto.class)))
    public ApiResponseDto<TokenDto> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS, authService.login(memberLoginRequestDto));
    }

//    @PostMapping("/reissue")
//    public ApiResponseDto<TokenDto> reissue(@RequestBody TokenDto tokenDto) {
//        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS, authService.reissue(tokenDto));
//    }
}
