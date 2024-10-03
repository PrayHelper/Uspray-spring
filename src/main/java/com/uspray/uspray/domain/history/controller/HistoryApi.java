package com.uspray.uspray.domain.history.controller;

import com.uspray.uspray.domain.history.dto.request.HistoryRequestDto;
import com.uspray.uspray.domain.history.dto.request.HistorySearchRequestDto;
import com.uspray.uspray.domain.history.dto.response.HistoryDetailResponseDto;
import com.uspray.uspray.domain.history.dto.response.HistoryListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.userdetails.User;

@Tag(name = "History", description = "기도제목 기록 API")
@SecurityRequirement(name = "JWT Auth")
public interface HistoryApi {

    ApiResponseDto<HistoryListResponseDto> getHistoryList(
        @Parameter(hidden = true) User user,
        HistoryRequestDto historyRequestDto);

    ApiResponseDto<HistoryListResponseDto> searchHistoryList(
        @Parameter(hidden = true) User user,
        HistorySearchRequestDto historySearchRequestDto
    );

    ApiResponseDto<HistoryDetailResponseDto> getHistoryDetail(
        @Parameter(hidden = true) User user,
        Long historyId);

    @Operation(summary = "히스토리 다시 기도하기")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 반환",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<PrayResponseDto> historyToPray(
        @Parameter(hidden = true) User user,
        Long historyId,
        PrayRequestDto prayRequestDto);
}
