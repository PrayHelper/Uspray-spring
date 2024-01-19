package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.group.request.GroupMemberRequestDto;
import com.uspray.uspray.DTO.group.request.GroupRequestDto;
import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupMemberResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.GroupFacade;
import com.uspray.uspray.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
@Tag(name = "Group", description = "모임 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class GroupController {

    private final GroupService groupService;
    private final GroupFacade groupFacade;

    @Operation(summary = "모임 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "사용자가 가입한 목록 조회",
        content = @Content(schema = @Schema(implementation = GroupListResponseDto.class))
    )
    @GetMapping
    public ApiResponseDto<GroupListResponseDto> getGroupList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_GROUP_LIST_SUCCESS,
            groupService.getGroupList(user.getUsername()));
    }

    @Operation(summary = "모임 생성")
    @ApiResponse(
        responseCode = "201",
        description = "신규로 모임을 생성합니다",
        content = @Content(schema = @Schema(implementation = GroupRequestDto.class))
    )
    @PostMapping
    public ApiResponseDto<?> createGroup(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Valid @RequestBody GroupRequestDto groupRequestDto) {
        groupFacade.createGroup(user.getUsername(), groupRequestDto);
        return ApiResponseDto.success(SuccessStatus.CREATE_GROUP_SUCCESS,
            SuccessStatus.CREATE_GROUP_SUCCESS.getMessage());
    }

    @Operation(summary = "[모임 리더] 모임 이름을 변경합니다")
    @ApiResponse(
        responseCode = "200",
        description = "모임 이름 변경",
        content = @Content(schema = @Schema(implementation = GroupRequestDto.class))
    )
    @PutMapping("/{groupId}/change-name")
    public ApiResponseDto<?> changeGroupName(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupRequestDto groupRequestDto) {
        groupFacade.changeGroupName(user.getUsername(), groupId, groupRequestDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_NAME_SUCCESS,
            SuccessStatus.CHANGE_GROUP_NAME_SUCCESS.getMessage());
    }

    @Operation(summary = "[모임 리더] 모임 리더를 위임합니다")
    @ApiResponse(
        responseCode = "200",
        description = "모임 리더 위임",
        content = @Content(schema = @Schema(implementation = GroupMemberRequestDto.class))
    )
    @PutMapping("/{groupId}/change-leader")
    public ApiResponseDto<?> changeGroupLeader(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupMemberRequestDto groupLeaderRequestDto) {
        groupFacade.changeGroupLeader(user.getUsername(), groupId,
            groupLeaderRequestDto.getMemberId());
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_LEADER_SUCCESS,
            SuccessStatus.CHANGE_GROUP_LEADER_SUCCESS.getMessage());
    }

    @Operation(summary = "[모임 리더] 회원 내보내기")
    @ApiResponse(
        responseCode = "200",
        description = "모임 회원을 내보냅니다",
        content = @Content(schema = @Schema(implementation = GroupMemberRequestDto.class))
    )
    @DeleteMapping("/{groupId}/kick")
    public ApiResponseDto<?> kickGroupMember(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupMemberRequestDto groupMemberRequestDto) {
        groupFacade.kickGroupMember(user.getUsername(), groupId,
            groupMemberRequestDto.getMemberId());
        return ApiResponseDto.success(SuccessStatus.KICK_GROUP_MEMBER_SUCCESS,
            SuccessStatus.KICK_GROUP_MEMBER_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 가입하기")
    @ApiResponse(
        responseCode = "200",
        description = "모임에 가입합니다"
    )
    @PostMapping("/{groupId}/join")
    public ApiResponseDto<?> addGroupMember(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.addGroupMember(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.ADD_GROUP_MEMBER_SUCCESS,
            SuccessStatus.ADD_GROUP_MEMBER_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 탈퇴하기")
    @ApiResponse(
        responseCode = "204",
        description = "모임을 떠납니다",
        content = @Content(schema = @Schema(implementation = GroupMemberRequestDto.class))
    )
    @DeleteMapping("/{groupId}/leave")
    public ApiResponseDto<?> leaveGroup(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.leaveGroup(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.LEAVE_GROUP_SUCCESS,
            SuccessStatus.LEAVE_GROUP_SUCCESS.getMessage());
    }

    @Operation(summary = "[모임 리더] 모임 삭제하기")
    @ApiResponse(
        responseCode = "200",
        description = "모임을 삭제합니다. 모임 회원, 모임 기도제목 등 일괄 삭제"
    )
    @DeleteMapping("/{groupId}")
    public ApiResponseDto<?> deleteGroup(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.deleteGroup(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.DELETE_GROUP_SUCCESS,
            SuccessStatus.DELETE_GROUP_SUCCESS.getMessage());
    }

    @Operation(summary = "회원 검색")
    @ApiResponse(
        responseCode = "200",
        description = "모임 내 회원 목록을 조회합니다"
    )
    @GetMapping("/{groupId}/member/search")
    public ApiResponseDto<List<GroupMemberResponseDto>> searchGroupMembers(
        @PathVariable Long groupId,
        @RequestParam(required = false) String name) {
        return ApiResponseDto.success(SuccessStatus.GET_MEMBER_LIST_SUCCESS,
            groupService.searchGroupMembers(groupId, name));
    }

    @Operation(summary = "그룹 알림 설정")
    @ApiResponse(
        responseCode = "200",
        description = "그룹 알림 설정을 변경합니다"
    )
    @PutMapping("/{groupId}/notification")
    public ApiResponseDto<?> changeGroupNotification(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.changeGroupNotification(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_NOTIFICATION_SUCCESS,
            SuccessStatus.CHANGE_GROUP_NOTIFICATION_SUCCESS.getMessage());
    }
}
