package com.uspray.uspray.global.auth.controller;

import com.uspray.uspray.DTO.auth.request.MemberRequestDto;
import com.uspray.uspray.DTO.auth.response.MemberNameResponseDto;
import com.uspray.uspray.global.auth.dto.member.TokenDto;
import com.uspray.uspray.global.auth.service.AuthService;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ApiResponse(
        responseCode = "201",
        description = "회원가입 성공",
        content = @Content(schema = @Schema(implementation = com.uspray.uspray.DTO.auth.response.MemberResponseDto.class)))
    public ApiResponseDto<com.uspray.uspray.DTO.auth.response.MemberResponseDto> signup(
        @RequestBody @Valid MemberRequestDto memberRequestDto) {
        return ApiResponseDto.success(SuccessStatus.SIGNUP_SUCCESS,
            authService.signup(memberRequestDto));
    }

    @PostMapping("/login")
    @ApiResponse(
        responseCode = "200",
        description = "로그인 성공",
        content = @Content(schema = @Schema(implementation = com.uspray.uspray.DTO.auth.response.MemberResponseDto.class)))
    public ApiResponseDto<TokenDto> login(
        @RequestBody @Valid com.uspray.uspray.DTO.auth.request.MemberLoginRequestDto memberLoginRequestDto) {
        return ApiResponseDto.success(SuccessStatus.LOGIN_SUCCESS,
            authService.login(memberLoginRequestDto));
    }

    @PostMapping("/reissue")
    @ApiResponse(
        responseCode = "200",
        description = "토큰 재발급 성공",
        content = @Content(schema = @Schema(implementation = TokenDto.class)))
    @SecurityRequirements({
        @SecurityRequirement(name = "Refresh")
    })
    public ApiResponseDto<TokenDto> reissue(@Parameter(hidden = true) HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh");
        return ApiResponseDto.success(SuccessStatus.REISSUE_SUCCESS,
            authService.reissue(refreshToken));
    }


    @PostMapping("/find-id")
    @Operation(summary = "아이디 찾기")
    public ApiResponseDto<String> findId(@RequestBody com.uspray.uspray.DTO.auth.request.FindIdDto findIdDto) {
        return ApiResponseDto.success(SuccessStatus.FIND_USER_ID_SUCCESS,
            authService.findId(findIdDto));
    }

    @PostMapping("/find-pw")
    @Operation(summary = "비밀번호 찾기")
    public ApiResponseDto<?> findPw(@RequestBody com.uspray.uspray.DTO.auth.request.FindPwDTO findPwDTO) {
        return ApiResponseDto.success(SuccessStatus.FIND_USER_PW_SUCCESS, authService.findPw(
            findPwDTO));
    }

    @PostMapping("/change-pw")
    @Operation(summary = "비밀번호 변경")
    public ApiResponseDto<?> changePw(@RequestBody com.uspray.uspray.DTO.auth.request.ChangePwDto changePwDto) {
        authService.changePw(changePwDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_USER_PW_SUCCESS);
    }

    @GetMapping("/name")
    @Operation(summary = "회원 이름 조회")
    @SecurityRequirement(name = "JWT Auth")
    public ApiResponseDto<MemberNameResponseDto> getName(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_USER_NAME_SUCCESS,
            authService.getMemberName(user.getUsername()));
    }

    @PostMapping("/withdrawal")
    @Operation(summary = "회원 탈퇴")
    @SecurityRequirement(name = "JWT Auth")
    public ApiResponseDto<?> withdrawal(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestBody com.uspray.uspray.DTO.auth.request.MemberDeleteDto memberDeleteDto) {
        authService.withdrawal(user.getUsername(), memberDeleteDto);
        return ApiResponseDto.success(SuccessStatus.WITHDRAWAL_SUCCESS);
    }

    @GetMapping("/dup-check/{userId}")
    @Operation(summary = "아이디 중복 체크")
    public ApiResponseDto<com.uspray.uspray.DTO.auth.response.DupCheckResponseDto> dupCheck(@PathVariable("userId") String userId) {
        return ApiResponseDto.success(SuccessStatus.CHECK_USER_ID_SUCCESS,
            authService.dupCheck(userId));

    }

    @GetMapping("/login-check")
    @Operation(summary = "사용한 로그인 방식 체크(일반, 소셜)")
    @SecurityRequirement(name = "JWT Auth")
    public ApiResponseDto<com.uspray.uspray.DTO.auth.response.LoginTypeResponseDto> loginCheck (@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.LOGIN_CHECK_SUCCESS, authService.loginCheck(user.getUsername()));
    }
}
