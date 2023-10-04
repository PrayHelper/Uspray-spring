package com.uspray.uspray.controller;


import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.sharedpray.response.SharedPrayListResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.ShareService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
@Tag(name = "shared pray", description = "기도제목 공유")
public class ShareController {

    private final ShareService shareService;

//    @GetMapping()
//    // 공유받은 기도제목 조회
//    public ApiResponseDto<SharedPrayListResponseDto> getSharedPrayList(
//            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
//        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS, shareService.getSharedPrayList(user.getUsername()));
//    }
}
