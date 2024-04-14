package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.history.request.HistoryRequestDto;
import com.uspray.uspray.DTO.history.request.HistorySearchRequestDto;
import com.uspray.uspray.DTO.history.response.HistoryDetailResponseDto;
import com.uspray.uspray.DTO.history.response.HistoryListResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.facade.HistoryFacade;
import com.uspray.uspray.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@Tag(name = "History", description = "기도제목 기록 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class HistoryController {

    private final HistoryService historyService;
    private final HistoryFacade historyFacade;

    @GetMapping
    public ApiResponseDto<HistoryListResponseDto> getHistoryList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @ModelAttribute HistoryRequestDto historyRequestDto) {
        return ApiResponseDto.success(SuccessStatus.GET_HISTORY_LIST_SUCCESS,
            historyService.getHistoryList(user.getUsername(), historyRequestDto.getType(),
                historyRequestDto.getPage(), historyRequestDto.getSize()));
    }

    // 이름, 내용, 카테고리에 해당되는 키워드 전부를 찾아서 검색
    // 내가 쓴 기도제목, 공유받은 기도제목 체크박스 (최소 한 개 이상 선택)
    // 날짜까지 (옵션)
    @PostMapping("/search")
    public ApiResponseDto<HistoryListResponseDto> searchHistoryList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestBody @Valid HistorySearchRequestDto historySearchRequestDto
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_HISTORY_LIST_SUCCESS,
            historyService.searchHistoryList(user.getUsername(), historySearchRequestDto));
    }

    @GetMapping("/detail/{historyId}")
    public ApiResponseDto<HistoryDetailResponseDto> getHistoryDetail(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long historyId) {
        return ApiResponseDto.success(SuccessStatus.GET_HISTORY_DETAIL_SUCCESS,
            historyService.getHistoryDetail(user.getUsername(), historyId));
    }

    @Operation(summary = "히스토리 다시 기도하기")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 반환",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @PostMapping("/pray/{historyId}")
    public ApiResponseDto<PrayResponseDto> historyToPray(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long historyId,
        @RequestBody @Valid PrayRequestDto prayRequestDto) {
        return ApiResponseDto.success(SuccessStatus.CREATE_PRAY_SUCCESS,
            historyFacade.historyToPray(prayRequestDto, user.getUsername(), historyId)
        );
    }
}
