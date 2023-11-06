package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.history.request.HistorySearchRequestDto;
import com.uspray.uspray.DTO.history.response.HistoryResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.HistoryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/history")
@Tag(name = "History", description = "기도제목 기록 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping()
    public ApiResponseDto<List<HistoryResponseDto>> getHistoryList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_HISTORY_LIST_SUCCESS,
            historyService.getHistoryList(user.getUsername()));
    }

    // 이름, 내용, 카테고리에 해당되는 키워드 전부를 찾아서 검색
    // 내가 쓴 기도제목, 공유받은 기도제목 체크박스 (최소 한 개 이상 선택)
    // 날짜까지 (옵션)
    @GetMapping("/search")
    public ApiResponseDto<List<HistoryResponseDto>> searchHistoryList(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isMine,
            @RequestParam(required = false) Boolean isShared,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ApiResponseDto.success(SuccessStatus.GET_HISTORY_LIST_SUCCESS,
            historyService.searchHistoryList(user.getUsername(), keyword, isMine, isShared, startDate, endDate));
    }
}
