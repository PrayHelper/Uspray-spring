package com.uspray.uspray.domain.pray.controller;


import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.service.PrayFacade;
import com.uspray.uspray.domain.pray.service.PrayService;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pray")
@RequiredArgsConstructor
public class PrayController implements PrayApi {

    private final PrayService prayService;
    private final PrayFacade prayFacade;

    @GetMapping
    public ApiResponseDto<List<PrayListResponseDto>> getPrayList(
        @AuthenticationPrincipal User user,
        String prayType
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
            prayFacade.getPrayList(user.getUsername(), prayType));
    }

    @GetMapping("/{prayId}")
    public ApiResponseDto<PrayResponseDto> getPrayDetail(
        @AuthenticationPrincipal User user,
        Long prayId
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_SUCCESS,
            prayService.getPrayDetail(prayId, user.getUsername()));
    }

    @PostMapping
    public ApiResponseDto<PrayResponseDto> createPray(
        @RequestBody @Valid PrayRequestDto prayRequestDto,
        @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.CREATE_PRAY_SUCCESS,
            prayFacade.createPray(prayRequestDto, user.getUsername(), null));
    }

    @DeleteMapping("/{prayId}")
    public ApiResponseDto<PrayResponseDto> deletePray(
        @PathVariable("prayId") Long prayId,
        @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.DELETE_PRAY_SUCCESS,
            prayFacade.deletePray(prayId, user.getUsername()));
    }

    @PutMapping("/{prayId}")
    public ApiResponseDto<PrayResponseDto> updatePray(
        @PathVariable("prayId") Long prayId,
        @RequestBody @Valid PrayUpdateRequestDto prayUpdateRequestDto,
        @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.UPDATE_PRAY_SUCCESS,
            prayFacade.updatePray(prayId, user.getUsername(), prayUpdateRequestDto));
    }

    @PutMapping("/{prayId}/today")
    public ApiResponseDto<List<PrayListResponseDto>> todayPray(
        @PathVariable("prayId") Long prayId,
        @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.INCREASE_PRAY_COUNT_SUCCESS,
            prayFacade.todayPray(prayId, user.getUsername()));
    }

    @PutMapping("/{prayId}/complete")
    public ApiResponseDto<List<PrayListResponseDto>> completePray(
        @PathVariable("prayId") Long prayId,
        @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
            prayFacade.completePray(prayId, user.getUsername()));
    }

    @PutMapping("/{prayId}/cancel")
    public ApiResponseDto<List<PrayListResponseDto>> cancelPray(
        @PathVariable("prayId") Long prayId,
        @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.CANCEL_PRAY_SUCCESS,
            prayFacade.cancelPray(prayId, user.getUsername()));
    }
}
