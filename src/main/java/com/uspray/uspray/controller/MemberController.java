package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@SecurityRequirement(name = "JWT Auth")
@Tag(name = "Member API", description = "유저 관련 api")
public class MemberController {

  private final MemberService memberService;

  @Operation(summary = "전화번호 변경")
  @PostMapping("/{changePhone}")
  public ApiResponseDto<?> changePhone(@AuthenticationPrincipal User user,
      @Schema(example = "01046518879") @PathVariable("changePhone") String changePhone) {
    memberService.changePhone(user.getUsername(), changePhone);
    return ApiResponseDto.success(SuccessStatus.CHANGE_PHONE_SUCCESS);
  }
}
