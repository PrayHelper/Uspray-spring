package com.uspray.uspray.domain.group.controller;

import com.uspray.uspray.domain.group.dto.group.request.GroupMemberRequestDto;
import com.uspray.uspray.domain.group.dto.group.request.GroupRequestDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupListResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupMemberResponseDto;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;

public interface GroupApi {

    @Operation(summary = "모임 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "사용자가 가입한 목록 조회",
        content = @Content(schema = @Schema(implementation = GroupListResponseDto.class))
    )
    ApiResponseDto<GroupListResponseDto> getGroupList(@Parameter(hidden = true) User user);

    @Operation(summary = "모임 생성")
    @ApiResponse(
        responseCode = "201",
        description = "신규로 모임을 생성합니다",
        content = @Content(schema = @Schema(implementation = GroupRequestDto.class))
    )
    ApiResponseDto<?> createGroup(
        @Parameter(hidden = true) User user,
        GroupRequestDto groupRequestDto
    );

    @Operation(summary = "[모임 리더] 모임 이름을 변경합니다")
    @ApiResponse(
        responseCode = "200",
        description = "모임 이름 변경",
        content = @Content(schema = @Schema(implementation = GroupRequestDto.class))
    )
    ApiResponseDto<?> changeGroupName(
        @Parameter(hidden = true) User user,
        Long groupId,
        GroupRequestDto groupRequestDto);

    @Operation(summary = "[모임 리더] 모임 리더를 위임합니다")
    @ApiResponse(
        responseCode = "200",
        description = "모임 리더 위임",
        content = @Content(schema = @Schema(implementation = GroupMemberRequestDto.class))
    )
    ApiResponseDto<?> changeGroupLeader(
        @Parameter(hidden = true) User user,
        Long groupId,
        GroupMemberRequestDto groupLeaderRequestDto);

    @Operation(summary = "[모임 리더] 회원 내보내기")
    @ApiResponse(
        responseCode = "200",
        description = "모임 회원을 내보냅니다",
        content = @Content(schema = @Schema(implementation = GroupMemberRequestDto.class))
    )
    ApiResponseDto<?> kickGroupMember(
        @Parameter(hidden = true) User user,
        Long groupId,
        GroupMemberRequestDto groupMemberRequestDto);

    @Operation(summary = "모임 가입하기")
    @ApiResponse(
        responseCode = "200",
        description = "모임에 가입합니다"
    )
    ApiResponseDto<?> addGroupMember(
        @Parameter(hidden = true) User user,
        Long groupId);

    @Operation(summary = "모임 탈퇴하기")
    @ApiResponse(
        responseCode = "204",
        description = "모임을 떠납니다",
        content = @Content(schema = @Schema(implementation = GroupMemberRequestDto.class))
    )
    ApiResponseDto<?> leaveGroup(
        @Parameter(hidden = true) User user,
        Long groupId);

    @Operation(summary = "[모임 리더] 모임 삭제하기")
    @ApiResponse(
        responseCode = "200",
        description = "모임을 삭제합니다. 모임 회원, 모임 기도제목 등 일괄 삭제"
    )
    ApiResponseDto<?> deleteGroup(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId);

    @Operation(summary = "회원 검색")
    @ApiResponse(
        responseCode = "200",
        description = "모임 내 회원 목록을 조회합니다"
    )
    ApiResponseDto<List<GroupMemberResponseDto>> searchGroupMembers(
        Long groupId,
        @Parameter(hidden = true) User user,
        String name);

    @Operation(summary = "그룹 알림 설정")
    @ApiResponse(
        responseCode = "200",
        description = "그룹 알림 설정을 변경합니다"
    )
    ApiResponseDto<?> changeGroupNotification(
        @Parameter(hidden = true) User user,
        Long groupId);
}
