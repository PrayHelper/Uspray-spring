package com.uspray.uspray.domain.group.controller;

import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayRappingDto;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayRequestDto;
import com.uspray.uspray.domain.group.dto.grouppray.ScrapRequestDto;
import com.uspray.uspray.domain.group.service.GroupPrayFacade;
import com.uspray.uspray.domain.group.service.GroupPrayService;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayToGroupPrayDto;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
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
@RequiredArgsConstructor
public class GroupPrayController implements GroupPrayApi{

    private final GroupPrayService groupPrayService;
    private final GroupPrayFacade groupPrayFacade;

    @GetMapping
    public ApiResponseDto<List<PrayListResponseDto>> getPrayList(
        @AuthenticationPrincipal User user,
        String prayType,
        Long groupId
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
            groupPrayFacade.getPrayList(user.getUsername(), prayType, groupId));
    }

    @PostMapping("/pray-to-grouppray")
    public ApiResponseDto<?> prayToGroupPray(
        @RequestBody PrayToGroupPrayDto prayToGroupPrayDto,
        @AuthenticationPrincipal User user
    ) {
        groupPrayFacade.prayToGroupPray(prayToGroupPrayDto, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.PRAY_TO_GROUP_PRAY_SUCCESS);
    }

    @PostMapping
    public ApiResponseDto<?> createGroupPray(@RequestBody GroupPrayRequestDto groupPrayRequestDto,
        @AuthenticationPrincipal User user) {
        groupPrayFacade.createGroupPray(groupPrayRequestDto, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.CREATE_GROUP_PRAY_SUCCESS);
    }

    @GetMapping("/{groupId}")
    public ApiResponseDto<GroupPrayRappingDto> getGroupPray(
        @PathVariable(name = "groupId") Long groupId,
        @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_GROUP_PRAY_LIST_SUCCESS,
            groupPrayFacade.getGroupPray(groupId, user.getUsername()));
    }

    @DeleteMapping("/{groupPrayId}")
    public ApiResponseDto<?> deleteGroupPray(@PathVariable(name = "groupPrayId") Long id) {
        groupPrayService.deleteGroupPray(id);
        return ApiResponseDto.success(SuccessStatus.DELETE_GROUP_PRAY_SUCCESS);
    }

    @PostMapping("/{groupPrayId}/like")
    public ApiResponseDto<?> likeGroupPray(@PathVariable(name = "groupPrayId") Long id,
        @AuthenticationPrincipal User user) {
        groupPrayFacade.heartGroupPray(id, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.LIKE_GROUP_PRAY_SUCCESS);
    }

    @PostMapping("/scrap")
    public ApiResponseDto<?> scarpGroupPray(@RequestBody ScrapRequestDto scrapRequestDto,
        @AuthenticationPrincipal User user) {
        groupPrayFacade.scrapGroupPray(scrapRequestDto, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.SCARP_GROUP_PRAY_SUCCESS);
    }
}
