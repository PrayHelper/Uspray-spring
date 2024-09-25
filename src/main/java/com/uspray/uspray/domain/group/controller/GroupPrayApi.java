package com.uspray.uspray.domain.group.controller;

import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayRappingDto;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayRequestDto;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.domain.group.dto.grouppray.ScrapRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayToGroupPrayDto;
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

@Tag(name = "Group pray", description = "모임 기도제목 API")
@SecurityRequirement(name = "JWT Auth")
public interface GroupPrayApi {

    @Operation(summary = "[모임 전용] 기도제목 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 목록 반환",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<List<PrayListResponseDto>> getPrayList(
        @Parameter(hidden = true) User user,
        @Parameter(description = "기도제목 종류(personal, shared)", required = true, example = "personal") String prayType,
        @Parameter(example = "1") Long groupId
    );

    @Operation(summary = "모임 기도제목으로 불러오기")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목으로 불러오기",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    ApiResponseDto<?> prayToGroupPray(
        PrayToGroupPrayDto prayToGroupPrayDto,
        @Parameter(hidden = true) User user
    );

    @Operation(summary = "모임 기도제목 생성")
    @ApiResponse(
        responseCode = "201",
        description = "모임 기도제목 생성")
    ApiResponseDto<?> createGroupPray(GroupPrayRequestDto groupPrayRequestDto,
        @Parameter(hidden = true) User user);

    @Operation(summary = "모임 기도제목 조회")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목 목록 반환",
        content = @Content(schema = @Schema(implementation = GroupPrayResponseDto.class)))
    ApiResponseDto<GroupPrayRappingDto> getGroupPray(
        Long groupId,
        @Parameter(hidden = true) User user);

    @Operation(summary = "모임 기도제목 삭제")
    @ApiResponse(
        responseCode = "204",
        description = "모임 기도제목 삭제")
    ApiResponseDto<?> deleteGroupPray(Long id);

    @Operation(summary = "모임 기도제목 좋아요")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목 좋아요")
    ApiResponseDto<?> likeGroupPray(Long id,
        @Parameter(hidden = true) User user);

    @Operation(summary = "모임 기도제목 스크랩")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목 스크랩")
    ApiResponseDto<?> scarpGroupPray(ScrapRequestDto scrapRequestDto,
        @Parameter(hidden = true) User user);
}
