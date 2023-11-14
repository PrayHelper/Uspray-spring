package com.uspray.uspray.controller;


import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayResponseDto;
import com.uspray.uspray.DTO.sharedpray.request.SharedPrayRequestDto;
import com.uspray.uspray.DTO.sharedpray.response.SharedPrayListResponseDto;
import com.uspray.uspray.DTO.sharedpray.response.SharedPrayResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.ShareService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
@Tag(name = "Shared pray", description = "기도제목 공유 관련 API")
public class ShareController {

    private final ShareService shareService;

    @GetMapping()
    @ApiResponse(
        responseCode = "200",
        description = "공유받은 기도제목 조회 (보관함 조회)",
        content = @Content(schema = @Schema(implementation = SharedPrayListResponseDto.class))
    )
    @Operation(summary = "공유받은 기도제목 조회 (보관함 조회)")
    public ApiResponseDto<List<SharedPrayResponseDto>> getSharedPrayList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
            shareService.getSharedPrayList(user.getUsername()));
    }

    @PostMapping()
    @ApiResponse(
        responseCode = "201",
        description = "기도제목 공유",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class))
    )
    @Operation(summary = "기도제목 공유")
    public ApiResponseDto<?> sharePray(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestBody SharedPrayRequestDto sharedPrayRequestDto) {
        shareService.sharePray(user.getUsername(), sharedPrayRequestDto);
        return ApiResponseDto.success(SuccessStatus.SHARE_PRAY_SUCCESS);
    }

    @DeleteMapping()
    @ApiResponse(
        responseCode = "204",
        description = "공유받은 기도제목 삭제",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class))
    )
    @Operation(summary = "공유받은 기도제목 삭제")
    public ApiResponseDto<?> deletePray(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestParam Long sharedPrayId) {
        shareService.deleteSharedPray(user.getUsername(), sharedPrayId);
        return ApiResponseDto.success(SuccessStatus.DELETE_PRAY_SUCCESS);
    }

    @PostMapping("/save")
    @ApiResponse(
        responseCode = "201",
        description = "공유받은 기도제목 저장",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class))
    )
    @Operation(summary = "공유받은 기도제목 저장")
    public ApiResponseDto<?> savePray(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestParam Long sharedPrayId) {
        shareService.saveSharedPray(user.getUsername(), sharedPrayId);
        return ApiResponseDto.success(SuccessStatus.SHARE_PRAY_AGREE_SUCCESS);
    }
}
