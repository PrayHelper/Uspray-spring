package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.auth.request.FindIdDto;
import com.uspray.uspray.DTO.auth.request.FindPwDto;
import com.uspray.uspray.DTO.auth.request.MemberLoginRequestDto;
import com.uspray.uspray.DTO.auth.request.MemberRequestDto;
import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.auth.TokenDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uspray.uspray.DTO.auth.response.MemberResponseDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "회원 관리", description = "Auth 관련 API docs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ApiResponse(
        responseCode = "201",
        description = "회원가입 성공",
        content = @Content(schema = @Schema(implementation = MemberResponseDto.class)))
    public ApiResponseDto<MemberResponseDto> signup(
        @RequestBody @Valid MemberRequestDto memberRequestDto) {
        return ApiResponseDto.success(SuccessStatus.SIGNUP_SUCCESS,
            authService.signup(memberRequestDto));
    }

    @PostMapping("/login")
    @ApiResponse(
        responseCode = "200",
        description = "로그인 성공",
        content = @Content(schema = @Schema(implementation = MemberResponseDto.class)))
    public ApiResponseDto<TokenDto> login(
        @RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS,
            authService.login(memberLoginRequestDto));
    }

//    @PostMapping("/reissue")
//    public ApiResponseDto<TokenDto> reissue(@RequestBody TokenDto tokenDto) {
//        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS, authService.reissue(tokenDto));
//    }


    @PostMapping("/find-id")
    @Operation(summary = "아이디 찾기")
    public ApiResponseDto<String> findId(@RequestBody FindIdDto findIdDto) {
        return ApiResponseDto.success(SuccessStatus.FIND_USER_ID_SUCCESS,
            authService.findId(findIdDto));
    }

    @PostMapping("/find-pw")
    @Operation(summary = "비밀번호 찾기")
    public ApiResponseDto<?> findId(@RequestBody FindPwDto findPwDto) {
        authService.findPw(findPwDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_USER_PW_SUCCESS);
    }

    @PostMapping("/withdrawal")
    @Operation(summary = "회원 탈퇴")
    @SecurityRequirement(name = "JWT Auth")
    public ApiResponseDto<?> withdrawal(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        authService.withdrawal(user.getUsername());
        return ApiResponseDto.success(SuccessStatus.WITHDRAWAL_SUCCESS);
    }

    @GetMapping("/dup-check/{userId}")
    @Operation(summary = "아이디 중복 체크")
    public ApiResponseDto<?> dupCheck(@PathVariable("userId") String userId) {
        authService.dupCheck(userId);
        return ApiResponseDto.success(SuccessStatus.CHECK_USER_ID_SUCCESS);

    }
}
