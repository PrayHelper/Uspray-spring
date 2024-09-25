package com.uspray.uspray.domain.category.controller;

import com.uspray.uspray.domain.category.dto.CategoryRequestDto;
import com.uspray.uspray.domain.category.dto.CategoryResponseDto;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.userdetails.User;

@Tag(name = "Category", description = "Category 관련 API")
@SecurityRequirement(name = "JWT Auth")
public interface CategoryApi {

    @Operation(summary = "카테고리 조회")
    @ApiResponse(
        responseCode = "200",
        description = "카테고리 조회",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    ApiResponseDto<CategoryResponseDto> getCategory(
        @Parameter(hidden = true) User user,
        @Parameter(description = "카테고리 ID", required = true) Long categoryId
    );

    @Operation(summary = "카테고리 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "카테고리 목록 조회",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    ApiResponseDto<List<CategoryResponseDto>> getCategoryList(
        @Parameter(hidden = true) User user,
        @Parameter(description = "카테고리 종류(personal, shared)", required = true, example = "personal") String categoryType
    );

    @Operation(summary = "카테고리 생성")
    @ApiResponse(
        responseCode = "201",
        description = "카테고리 생성",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    ApiResponseDto<CategoryResponseDto> createCategory(
        @Parameter(hidden = true) User user,
        CategoryRequestDto categoryRequestDto
    );

    ApiResponseDto<CategoryResponseDto> deleteCategory(
        @Parameter(hidden = true) User user,
        @Parameter(description = "카테고리 ID", required = true) Long categoryId
    );

    @Operation(summary = "카테고리 수정")
    @ApiResponse(
        responseCode = "200",
        description = "카테고리 수정",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    ApiResponseDto<CategoryResponseDto> updatePray(
        @Parameter(description = "카테고리 ID", required = true) Long categoryId,
        CategoryRequestDto categoryRequestDto,
        @Parameter(hidden = true) User user
    );

    @Operation(summary = "카테고리 순서 수정")
    @ApiResponse(
        responseCode = "200",
        description = "카테고리 순서 수정",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    ApiResponseDto<CategoryResponseDto> updatePrayOrder(
        @Parameter(description = "카테고리 ID", required = true) Long categoryId,
        @Parameter(description = "카테고리 순서", required = true) int index,
        @Parameter(hidden = true) User user
    );

}
