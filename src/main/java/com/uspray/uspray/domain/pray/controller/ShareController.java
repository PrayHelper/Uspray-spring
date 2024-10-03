package com.uspray.uspray.domain.pray.controller;


import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPrayDeleteRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPrayRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPraySaveRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.response.SharedPrayResponseDto;
import com.uspray.uspray.domain.pray.service.ShareFacade;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
public class ShareController implements ShareApi {

    private final ShareFacade shareFacade;

    @GetMapping
    public ApiResponseDto<List<SharedPrayResponseDto>> getSharedPrayList(
        @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
            shareFacade.getSharedPrayList(user.getUsername()));
    }

    @PostMapping("/receive")
    public ApiResponseDto<Long> receiveSharedPray(
        @AuthenticationPrincipal User user,
        @RequestBody SharedPrayRequestDto sharedPrayRequestDto) {
        return ApiResponseDto.success(SuccessStatus.SHARE_PRAY_SUCCESS,
            shareFacade.receivedSharedPray(user.getUsername(), sharedPrayRequestDto));
    }

    @DeleteMapping
    public ApiResponseDto<?> deletePray(
        @AuthenticationPrincipal User user,
        @RequestBody SharedPrayDeleteRequestDto sharedPrayDeleteRequestDto) {
        shareFacade.deleteSharedPray(user.getUsername(), sharedPrayDeleteRequestDto);
        return ApiResponseDto.success(SuccessStatus.DELETE_PRAY_SUCCESS, SuccessStatus.DELETE_PRAY_SUCCESS.getMessage());
    }

    @PostMapping("/save")
    public ApiResponseDto<?> savePray(
        @AuthenticationPrincipal User user,
        @RequestBody SharedPraySaveRequestDto sharedPraySaveRequestDto) {
        shareFacade.saveSharedPray(user.getUsername(), sharedPraySaveRequestDto);
        return ApiResponseDto.success(SuccessStatus.SHARE_PRAY_AGREE_SUCCESS, SuccessStatus.SHARE_PRAY_AGREE_SUCCESS.getMessage());
    }
}
