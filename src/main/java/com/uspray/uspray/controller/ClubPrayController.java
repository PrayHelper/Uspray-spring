package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.clubpray.ClubPrayRequestDto;
import com.uspray.uspray.DTO.clubpray.ClubPrayResponseDto;
import com.uspray.uspray.DTO.clubpray.ClubPrayUpdateDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.ClubPrayService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clubpray")
@Tag(name = "ClubPray", description = "모임 기도제목 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class ClubPrayController {

    private final ClubPrayService clubPrayService;

    @Operation(summary = "모임 기도제목 생성")
    @PostMapping
    public ApiResponseDto<?> createClubPray(@RequestBody ClubPrayRequestDto clubPrayRequestDto,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        clubPrayService.createClubPray(clubPrayRequestDto, user.getUsername());
        return ApiResponseDto.success(SuccessStatus.CREATE_CLUB_PRAY_SUCCESS,
            SuccessStatus.CREATE_CLUB_PRAY_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 기도제목 수정")
    @PostMapping("/update")
    public ApiResponseDto<?> updateClubPray(@RequestBody ClubPrayUpdateDto clubPrayUpdateDto) {
        clubPrayService.updateClubPray(clubPrayUpdateDto);
        return ApiResponseDto.success(SuccessStatus.UPDATE_CLUB_PRAY_SUCCESS,
            SuccessStatus.UPDATE_CLUB_PRAY_SUCCESS.getMessage());
    }

    @Operation(summary = "모임 기도제목 조회")
    @GetMapping("/{club-id}")
    @ApiResponse(
        responseCode = "200",
        description = "모임 기도제목 목록 반환",
        content = @Content(schema = @Schema(implementation = ClubPrayResponseDto.class)))
    public ApiResponseDto<List<ClubPrayResponseDto>> getClubPray(@PathVariable(name = "club-id") Long clubId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_CLUB_PRAY_LIST_SUCCESS,
            clubPrayService.getClubPray(clubId, user.getUsername()));
    }
}
