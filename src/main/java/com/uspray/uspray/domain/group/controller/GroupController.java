package com.uspray.uspray.domain.group.controller;

import com.uspray.uspray.domain.group.dto.group.request.GroupMemberRequestDto;
import com.uspray.uspray.domain.group.dto.group.request.GroupRequestDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupListResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupMemberResponseDto;
import com.uspray.uspray.domain.group.service.GroupFacade;
import com.uspray.uspray.domain.group.service.GroupService;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequiredArgsConstructor
public class GroupController implements GroupApi {

    private final GroupService groupService;
    private final GroupFacade groupFacade;

    @GetMapping
    public ApiResponseDto<GroupListResponseDto> getGroupList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_GROUP_LIST_SUCCESS,
            groupService.getGroupList(user.getUsername()));
    }

    @PostMapping
    public ApiResponseDto<?> createGroup(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody GroupRequestDto groupRequestDto) {
        groupFacade.createGroup(user.getUsername(), groupRequestDto);
        return ApiResponseDto.success(SuccessStatus.CREATE_GROUP_SUCCESS,
            SuccessStatus.CREATE_GROUP_SUCCESS.getMessage());
    }

    @PutMapping("/{groupId}/change-name")
    public ApiResponseDto<?> changeGroupName(
        @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupRequestDto groupRequestDto) {
        groupFacade.changeGroupName(user.getUsername(), groupId, groupRequestDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_NAME_SUCCESS,
            SuccessStatus.CHANGE_GROUP_NAME_SUCCESS.getMessage());
    }

    @PutMapping("/{groupId}/change-leader")
    public ApiResponseDto<?> changeGroupLeader(
        @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupMemberRequestDto groupLeaderRequestDto) {
        groupFacade.changeGroupLeader(user.getUsername(), groupId,
            groupLeaderRequestDto.getMemberId());
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_LEADER_SUCCESS,
            SuccessStatus.CHANGE_GROUP_LEADER_SUCCESS.getMessage());
    }

    @DeleteMapping("/{groupId}/kick")
    public ApiResponseDto<?> kickGroupMember(
        @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupMemberRequestDto groupMemberRequestDto) {
        groupFacade.kickGroupMember(user.getUsername(), groupId,
            groupMemberRequestDto.getMemberId());
        return ApiResponseDto.success(SuccessStatus.KICK_GROUP_MEMBER_SUCCESS,
            SuccessStatus.KICK_GROUP_MEMBER_SUCCESS.getMessage());
    }

    @PostMapping("/{groupId}/join")
    public ApiResponseDto<?> addGroupMember(
        @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.addGroupMember(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.ADD_GROUP_MEMBER_SUCCESS,
            SuccessStatus.ADD_GROUP_MEMBER_SUCCESS.getMessage());
    }

    @DeleteMapping("/{groupId}/leave")
    public ApiResponseDto<?> leaveGroup(
        @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.leaveGroup(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.LEAVE_GROUP_SUCCESS,
            SuccessStatus.LEAVE_GROUP_SUCCESS.getMessage());
    }

    @DeleteMapping("/{groupId}")
    public ApiResponseDto<?> deleteGroup(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.deleteGroup(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.DELETE_GROUP_SUCCESS,
            SuccessStatus.DELETE_GROUP_SUCCESS.getMessage());
    }

    @GetMapping("/{groupId}/member/search")
    public ApiResponseDto<List<GroupMemberResponseDto>> searchGroupMembers(
        @PathVariable Long groupId,
        @AuthenticationPrincipal User user,
        @RequestParam(required = false) String name) {
        return ApiResponseDto.success(SuccessStatus.GET_MEMBER_LIST_SUCCESS,
            groupService.searchGroupMembers(groupId, name, user.getUsername()));
    }

    @PutMapping("/{groupId}/notification")
    public ApiResponseDto<?> changeGroupNotification(
        @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.changeGroupNotification(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_NOTIFICATION_SUCCESS,
            SuccessStatus.CHANGE_GROUP_NOTIFICATION_SUCCESS.getMessage());
    }
}
