package com.uspray.uspray.controller;


import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.PrayService;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pray")
@Tag(name = "Pray", description = "기도제목 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class PrayController {

  private final PrayService prayService;

  @Operation(summary = "기도제목 목록 조회")

  @ApiResponse(
      responseCode = "200",
      description = "기도제목 목록 반환",
      content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))

  @GetMapping()
  public ApiResponseDto<List<PrayResponseDto>> getPrayList(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @Parameter(description = "정렬 기준 (date, count)", required = true, example = "date") String orderType
  ) {
    return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
        prayService.getPrayList(user.getUsername(), orderType));
  }

  @GetMapping("/{prayId}")
  @ApiResponse(
      responseCode = "200",
      description = "기도제목 조회",
      content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
  @Operation(summary = "기도제목 조회")
  public ApiResponseDto<PrayResponseDto> getPrayDetail(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId
  ) {
    return ApiResponseDto.success(SuccessStatus.GET_PRAY_SUCCESS,
        prayService.getPrayDetail(prayId, user.getUsername()));
  }

  @PostMapping()
  @ApiResponse(
      responseCode = "201",
      description = "기도제목 생성",
      content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
  @Operation(summary = "기도제목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponseDto<PrayResponseDto> createPray(
      @RequestBody @Valid PrayRequestDto prayRequestDto,
      @Parameter(hidden = true) @AuthenticationPrincipal User user
  ) {
    return ApiResponseDto.success(SuccessStatus.CREATE_PRAY_SUCCESS,
        prayService.createPray(prayRequestDto, user.getUsername()));
  }

  @DeleteMapping("/{prayId}")
  @ApiResponse(responseCode = "204", description = "기도제목 삭제")
  @Operation(summary = "기도제목 삭제")
  public ApiResponseDto<PrayResponseDto> deletePray(
      @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user
  ) {
    return ApiResponseDto.success(SuccessStatus.DELETE_PRAY_SUCCESS,
        prayService.deletePray(prayId, user.getUsername()));
  }

  @PutMapping("/{prayId}")
  @ApiResponse(
      responseCode = "200",
      description = "기도제목 수정",
      content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
  @Operation(summary = "기도제목 수정")
  public ApiResponseDto<PrayResponseDto> updatePray(
      @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId,
      @RequestBody @Valid PrayRequestDto prayRequestDto,
      @Parameter(hidden = true) @AuthenticationPrincipal User user
  ) {
    return ApiResponseDto.success(SuccessStatus.UPDATE_PRAY_SUCCESS,
        prayService.updatePray(prayId, user.getUsername(), prayRequestDto));
  }
}
