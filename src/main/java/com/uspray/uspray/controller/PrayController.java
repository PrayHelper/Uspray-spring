package com.uspray.uspray.controller;


import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.pray.PrayDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.PrayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pray")
@Tag(name = "Pray", description = "기도제목 API")
@RequiredArgsConstructor
public class PrayController {

  private final PrayService prayService;

  @Operation(summary = "기도제목 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "기도제목 목록 반환",
      content = @Content(schema = @Schema(implementation = PrayDto.class)))
  @GetMapping()
  public ApiResponseDto<PrayDto> getPrayList() {
    return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS, null);
  }

  @GetMapping("/{prayId}")
  @ApiResponse(
      responseCode = "200",
      description = "기도제목 조회",
      content = @Content(schema = @Schema(implementation = PrayDto.class)))
  @Operation(summary = "기도제목 조회")
  public ApiResponseDto<PrayDto> getPrayDetail(
      @Parameter(description = "기도제목 ID", required = true) Long prayId
  ) {
    return ApiResponseDto.success(SuccessStatus.GET_PRAY_SUCCESS, null);
  }

  @PostMapping()
  @ApiResponse(
      responseCode = "201",
      description = "기도제목 생성",
      content = @Content(schema = @Schema(implementation = PrayDto.class)))
  @Operation(summary = "기도제목 생성")
  public ApiResponseDto<PrayDto> createPray(
      @RequestBody @Valid PrayRequestDto prayRequestDto
  ) {
    return ApiResponseDto.success(SuccessStatus.CREATE_PRAY_SUCCESS,
        prayService.createPray(prayRequestDto));
  }

  @DeleteMapping("/{prayId}")
  @ApiResponse(responseCode = "204", description = "기도제목 삭제")
  @Operation(summary = "기도제목 삭제")
  public ApiResponseDto<String> deletePray(
      @Parameter(description = "기도제목 ID", required = true) Long prayId
  ) {
    return ApiResponseDto.success(SuccessStatus.DELETE_PRAY_SUCCESS, null);
  }
}
