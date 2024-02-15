package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayRappingDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayRequestDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.DTO.grouppray.ScrapRequestDto;
import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.GroupPrayFacade;
import com.uspray.uspray.service.GroupPrayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grouppray")
@Tag(name = "Group pray", description = "모임 기도제목 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class GroupPrayController {

    private final GroupPrayService groupPrayService;
    private final GroupPrayFacade groupPrayFacade;

    @Operation(summary = "[모임 전용] 기도제목 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 목록 반환",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @GetMapping
    public ApiResponseDto<List<PrayListResponseDto>> getPrayList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Parameter(description = "기도제목 종류(personal, shared)", required = true, example = "personal") String prayType,
        @Parameter(example = "1") Long groupId
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
            groupPrayFacade.getPrayList(user.getUsername(), prayType, groupId));
    }

    @Operation(summary = "모임 기도제목 생성")
    @PostMapping
    @ApiResponse(
        responseCode = "201",
        description = "모임 기도제목 생성")
    public ApiResponseDto<?> createGroupPray(@RequestBody GroupPrayRequestDto groupPrayRequestDto,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        groupPrayFacade.createGroupPray(groupPrayRequestDto, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.CREATE_GROUP_PRAY_SUCCESS,
            SuccessStatus.CREATE_GROUP_PRAY_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 기도제목 조회")
    @GetMapping("/{groupId}")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목 목록 반환",
        content = @Content(schema = @Schema(implementation = GroupPrayResponseDto.class)))
    public ApiResponseDto<GroupPrayRappingDto> getGroupPray(
        @PathVariable(name = "groupId") Long groupId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_GROUP_PRAY_LIST_SUCCESS,
            groupPrayFacade.getGroupPray(groupId, user.getUsername()));
    }

    @Operation(summary = "모임 기도제목 삭제")
    @DeleteMapping("/{groupPrayId}")
    @ApiResponse(
        responseCode = "204",
        description = "모임 기도제목 삭제")
    public ApiResponseDto<?> deleteGroupPray(@PathVariable(name = "groupPrayId") Long id) {
        groupPrayService.deleteGroupPray(id);
        return ApiResponseDto.success(SuccessStatus.DELETE_GROUP_PRAY_SUCCESS,
            SuccessStatus.DELETE_GROUP_PRAY_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 기도제목 좋아요")
    @PostMapping("/{groupPrayId}/like")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목 좋아요")
    public ApiResponseDto<?> likeGroupPray(@PathVariable(name = "groupPrayId") Long id,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        groupPrayFacade.heartGroupPray(id, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.LIKE_GROUP_PRAY_SUCCESS,
            SuccessStatus.LIKE_GROUP_PRAY_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 기도제목 스크랩")
    @PostMapping("/scrap")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목 스크랩")
    public ApiResponseDto<?> scarpGroupPray(@RequestBody ScrapRequestDto scrapRequestDto,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        groupPrayFacade.scrapGroupPray(scrapRequestDto, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.SCARP_GROUP_PRAY_SUCCESS,
            SuccessStatus.SCARP_GROUP_PRAY_SUCCESS.getMessage());
    }
}
