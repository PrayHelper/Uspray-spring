package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.GroupService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
