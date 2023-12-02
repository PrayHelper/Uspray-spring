package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.group.request.GroupMemberRequestDto;
import com.uspray.uspray.DTO.group.request.GroupRequestDto;
import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupMemberResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.GroupFacade;
import com.uspray.uspray.service.GroupService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/group")
@Tag(name = "Group", description = "모임 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class GroupController {

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
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Valid @RequestBody GroupRequestDto groupRequestDto) {
        groupFacade.createGroup(user.getUsername(), groupRequestDto);
        return ApiResponseDto.success(SuccessStatus.CREATE_GROUP_SUCCESS,
            SuccessStatus.CREATE_GROUP_SUCCESS.getMessage());
    }

    @PutMapping("/{groupId}/change-name")
    public ApiResponseDto<?> changeGroupName(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupRequestDto groupRequestDto) {
        groupFacade.changeGroupName(user.getUsername(), groupId, groupRequestDto);
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_NAME_SUCCESS,
            SuccessStatus.CHANGE_GROUP_NAME_SUCCESS.getMessage());
    }

    @PutMapping("/{groupId}/change-leader")
    public ApiResponseDto<?> changeGroupLeader(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupMemberRequestDto groupLeaderRequestDto) {
        groupFacade.changeGroupLeader(user.getUsername(), groupId, groupLeaderRequestDto.getMemberId());
        return ApiResponseDto.success(SuccessStatus.CHANGE_GROUP_LEADER_SUCCESS,
            SuccessStatus.CHANGE_GROUP_LEADER_SUCCESS.getMessage());
    }

    @DeleteMapping("/{groupId}/kick")
    public ApiResponseDto<?> kickGroupMember(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId,
        @Valid @RequestBody GroupMemberRequestDto groupMemberRequestDto) {
        groupFacade.kickGroupMember(user.getUsername(), groupId, groupMemberRequestDto.getMemberId());
        return ApiResponseDto.success(SuccessStatus.KICK_GROUP_MEMBER_SUCCESS,
            SuccessStatus.KICK_GROUP_MEMBER_SUCCESS.getMessage());
    }

    @PostMapping("/{groupId}/join")
    public ApiResponseDto<?> addGroupMember(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable Long groupId) {
        groupFacade.addGroupMember(user.getUsername(), groupId);
        return ApiResponseDto.success(SuccessStatus.ADD_GROUP_MEMBER_SUCCESS,
            SuccessStatus.ADD_GROUP_MEMBER_SUCCESS.getMessage());
    }

    @DeleteMapping("/{groupId}/leave")
    public ApiResponseDto<?> leaveGroup(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
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
        @RequestParam(required = false) String name) {
        return ApiResponseDto.success(SuccessStatus.GET_MEMBER_LIST_SUCCESS,
            groupService.searchGroupMembers(groupId, name));
    }
}
