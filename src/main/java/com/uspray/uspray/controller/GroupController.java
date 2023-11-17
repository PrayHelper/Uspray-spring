package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.group.request.GroupKickRequestDto;
import com.uspray.uspray.DTO.group.request.GroupLeaderRequestDto;
import com.uspray.uspray.DTO.group.request.GroupRequestDto;
import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.GroupService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/group")
@Tag(name = "Group", description = "모임 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ApiResponseDto<GroupListResponseDto> getGroupList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_GROUP_LIST_SUCCESS,
            groupService.getGroupList(user.getUsername()));
    }

    @PostMapping
    public ApiResponseDto<?> createGroup(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Valid @RequestBody GroupRequestDto groupRequestDto) {
        groupService.createGroup(user.getUsername(), groupRequestDto);
        return ApiResponseDto.success(SuccessStatus.CREATE_GROUP_SUCCESS,
            SuccessStatus.CREATE_GROUP_SUCCESS.getMessage());
    }

    @PutMapping("/{groupId}/change-name")
    public ApiResponseDto<?> changeGroupName(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupRequestDto groupRequestDto) {
        groupService.changeGroupName(user.getUsername(), groupId, groupRequestDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_NAME_SUCCESS,
            SuccessStatus.CHANGE_GROUP_NAME_SUCCESS.getMessage());
    }

    @PutMapping("/{groupId}/change-leader")
    public ApiResponseDto<?> changeGroupLeader(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupLeaderRequestDto groupLeaderRequestDto) {
        groupService.changeGroupLeader(user.getUsername(), groupId, groupLeaderRequestDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_LEADER_SUCCESS,
            SuccessStatus.CHANGE_GROUP_LEADER_SUCCESS.getMessage());
    }

    @DeleteMapping("/{groupId}/kick")
    public ApiResponseDto<?> kickGroupMember(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupKickRequestDto groupKickRequestDto) {
        groupService.kickGroupMember(user.getUsername(), groupId, groupKickRequestDto);
        return ApiResponseDto.success(SuccessStatus.KICK_GROUP_MEMBER_SUCCESS,
            SuccessStatus.KICK_GROUP_MEMBER_SUCCESS.getMessage());
    }

    @DeleteMapping("/{groupId}")
    public ApiResponseDto<?> deleteGroup(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupService.deleteGroup(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.DELETE_GROUP_SUCCESS,
            SuccessStatus.DELETE_GROUP_SUCCESS.getMessage());
    }
}
