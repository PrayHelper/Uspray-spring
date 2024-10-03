package com.uspray.uspray.domain.pray.controller;

import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPrayDeleteRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPrayRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPraySaveRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.response.SharedPrayResponseDto;
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

@SecurityRequirement(name = "JWT Auth")
@Tag(name = "Shared pray", description = "기도제목 공유 관련 API")
public interface ShareApi {

    @ApiResponse(
        responseCode = "200",
        description = "공유받은 기도제목 조회 (보관함 조회)",
        content = @Content(schema = @Schema(implementation = SharedPrayRequestDto.class))
    )
    @Operation(summary = "공유받은 기도제목 조회 (보관함 조회)")
    ApiResponseDto<List<SharedPrayResponseDto>> getSharedPrayList(
        @Parameter(hidden = true) User user);

    @ApiResponse(
        responseCode = "201",
        description = "공유받은 기도제목을 보관함에 넣습니다",
        content = @Content(schema = @Schema(implementation = SharedPrayRequestDto.class))
    )
    @Operation(summary = "기도제목 공유받기")
    ApiResponseDto<Long> receiveSharedPray(
        @Parameter(hidden = true) User user,
        SharedPrayRequestDto sharedPrayRequestDto);

    @ApiResponse(
        responseCode = "204",
        description = "공유받은 기도제목 삭제",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class))
    )
    @Operation(summary = "공유받은 기도제목 삭제")
    ApiResponseDto<?> deletePray(
        @Parameter(hidden = true) User user,
        SharedPrayDeleteRequestDto sharedPrayDeleteRequestDto);

    @ApiResponse(
        responseCode = "201",
        description = "보관함에 있는 공유받은 기도제목을 개인 기도제목으로 저장합니다",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class))
    )
    @Operation(summary = "공유받은 기도제목 저장")
    ApiResponseDto<?> savePray(
        @Parameter(hidden = true) User user,
        SharedPraySaveRequestDto sharedPraySaveRequestDto);

}
