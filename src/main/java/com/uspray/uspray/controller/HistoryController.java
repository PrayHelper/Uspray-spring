package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.history.response.HistoryDetailResponseDto;
import com.uspray.uspray.DTO.history.response.HistoryListResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.HistoryService;
import com.uspray.uspray.service.PrayService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@Tag(name = "History", description = "기도제목 기록 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class HistoryController {
    
    private final HistoryService historyService;
    private final PrayService prayService;
    
    @GetMapping
    public ApiResponseDto<HistoryListResponseDto> getHistoryList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestParam(value = "type", defaultValue = "personal") String type,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {
        return ApiResponseDto.success(SuccessStatus.GET_HISTORY_LIST_SUCCESS,
            historyService.getHistoryList(user.getUsername(), type, page, size));
    }
    
    // 이름, 내용, 카테고리에 해당되는 키워드 전부를 찾아서 검색
    // 내가 쓴 기도제목, 공유받은 기도제목 체크박스 (최소 한 개 이상 선택)
    // 날짜까지 (옵션)
    @GetMapping("/search")
    public ApiResponseDto<HistoryListResponseDto> searchHistoryList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestParam(required = false) String keyword,
        @RequestParam Boolean isPersonal,
        @RequestParam Boolean isShared,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {
        return ApiResponseDto.success(SuccessStatus.GET_HISTORY_LIST_SUCCESS,
            historyService.searchHistoryList(user.getUsername(), keyword, isPersonal, isShared,
                startDate, endDate, page, size));
    }
    
    @GetMapping("/detail/{historyId}")
    public ApiResponseDto<HistoryDetailResponseDto> getHistoryDetail(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long historyId) {
        return ApiResponseDto.success(SuccessStatus.GET_HISTORY_DETAIL_SUCCESS,
            historyService.getHistoryDetail(user.getUsername(), historyId));
    }
    
    @PostMapping("/pray/{historyId}")
    public ApiResponseDto<HistoryListResponseDto> createPray(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long historyId,
        @RequestBody @Valid PrayRequestDto prayRequestDto) {
        prayService.createPray(prayRequestDto, user.getUsername());
        historyService.deleteHistory(historyId, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.CREATE_PRAY_SUCCESS,
            historyService.getHistoryList(user.getUsername(), "PERSONAL", 0, 10));
    }
}
