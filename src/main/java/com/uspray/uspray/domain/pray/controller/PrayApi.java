package com.uspray.uspray.domain.pray.controller;

import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.userdetails.User;

@Tag(name = "Pray", description = "기도제목 관련 API")
@SecurityRequirement(name = "JWT Auth")
public interface PrayApi {

    @Operation(summary = "기도제목 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 목록 반환",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<List<PrayListResponseDto>> getPrayList(
        @Parameter(hidden = true) User user,
        @Parameter(description = "기도제목 종류(personal, shared)", required = true, example = "personal") String prayType
    );

    @Operation(summary = "기도제목 조회")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 조회",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<PrayResponseDto> getPrayDetail(
        @Parameter(hidden = true) User user,
        @Parameter(description = "기도제목 ID", required = true) Long prayId
    );

    @Operation(summary = "기도제목 생성")
    @ApiResponse(
        responseCode = "201",
        description = "기도제목 생성",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<PrayResponseDto> createPray(
        PrayRequestDto prayRequestDto,
        @Parameter(hidden = true) User user
    );

    @ApiResponse(responseCode = "204", description = "기도제목 삭제")
    @Operation(summary = "기도제목 삭제")
    ApiResponseDto<PrayResponseDto> deletePray(
        @Parameter(description = "기도제목 ID", required = true) Long prayId,
        @Parameter(hidden = true) User user
    );

    @Operation(summary = "기도제목 수정")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 수정",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<PrayResponseDto> updatePray(
        @Parameter(description = "기도제목 ID", required = true) Long prayId,
        PrayUpdateRequestDto prayUpdateRequestDto,
        @Parameter(hidden = true) User user
    );

    @Operation(summary = "오늘 기도하기")
    @ApiResponse(
        responseCode = "200",
        description = "오늘 기도하기",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<List<PrayListResponseDto>> todayPray(
        @Parameter(description = "기도제목 ID", required = true) Long prayId,
        @Parameter(hidden = true) User user
    );

    @Operation(summary = "기도 완료하기")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 완료하기",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<List<PrayListResponseDto>> completePray(
        @Parameter(description = "기도제목 ID", required = true) Long prayId,
        @Parameter(hidden = true) User user
    );

    @Operation(summary = "기도제목 취소하기")
    @ApiResponse(
        responseCode = "200",
        description = "오늘 기도 취소하기",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<List<PrayListResponseDto>> cancelPray(
        @Parameter(description = "기도제목 ID", required = true) Long prayId,
        @Parameter(hidden = true) User user
    );
}
