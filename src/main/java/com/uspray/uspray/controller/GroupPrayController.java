package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayRequestDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayUpdateDto;
import com.uspray.uspray.exception.SuccessStatus;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grouppray")
@Tag(name = "GroupPray", description = "모임 기도제목 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class GroupPrayController {

    private final GroupPrayService groupPrayService;

    @Operation(summary = "모임 기도제목 생성")
    @PostMapping
    public ApiResponseDto<?> createGroupPray(@RequestBody GroupPrayRequestDto groupPrayRequestDto,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        groupPrayService.createGroupPray(groupPrayRequestDto, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.CREATE_GROUP_PRAY_SUCCESS,
            SuccessStatus.CREATE_GROUP_PRAY_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 기도제목 수정")
    @PutMapping
    public ApiResponseDto<?> updateGroupPray(@RequestBody GroupPrayUpdateDto groupPrayUpdateDto) {
        groupPrayService.updateGroupPray(groupPrayUpdateDto);
        return ApiResponseDto.success(SuccessStatus.UPDATE_GROUP_PRAY_SUCCESS,
            SuccessStatus.UPDATE_GROUP_PRAY_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 기도제목 조회")
    @GetMapping("/{groupId}")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목 목록 반환",
        content = @Content(schema = @Schema(implementation = GroupPrayResponseDto.class)))
    public ApiResponseDto<List<GroupPrayResponseDto>> getGroupPray(
        @PathVariable(name = "groupId") Long groupId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_GROUP_PRAY_LIST_SUCCESS,
            groupPrayService.getGroupPray(groupId, user.getUsername()));
    }

    @Operation(summary = "모임 기도제목 삭제")
    @DeleteMapping("/{groupPrayId}")
    public ApiResponseDto<?> deleteGroupPray(@PathVariable(name = "groupPrayId") Long id) {
        groupPrayService.deleteGroupPray(id);
        return ApiResponseDto.success(SuccessStatus.DELETE_GROUP_PRAY_SUCCESS,
            SuccessStatus.DELETE_GROUP_PRAY_SUCCESS.getMessage());
    }
}
