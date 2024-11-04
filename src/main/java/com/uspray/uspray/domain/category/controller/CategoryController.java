package com.uspray.uspray.domain.category.controller;

import com.uspray.uspray.domain.category.dto.CategoryRequestDto;
import com.uspray.uspray.domain.category.dto.CategoryResponseDto;
import com.uspray.uspray.domain.category.service.CategoryService;
import com.uspray.uspray.domain.pray.service.PrayFacade;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {
    private final CategoryService categoryService;
    private final PrayFacade prayFacade;


    @GetMapping("/{categoryId}")
    public ApiResponseDto<CategoryResponseDto> getCategory(
        @AuthenticationPrincipal User user, @PathVariable("categoryId") Long categoryId
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_CATEGORY_SUCCESS,
            categoryService.getCategory(user.getUsername(), categoryId));
    }

    @GetMapping
    public ApiResponseDto<List<CategoryResponseDto>> getCategoryList(
        @AuthenticationPrincipal User user, String categoryType
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_CATEGORY_LIST_SUCCESS,
            categoryService.getCategoryList(user.getUsername(), categoryType));
    }

    @PostMapping
    public ApiResponseDto<CategoryResponseDto> createCategory(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid CategoryRequestDto categoryRequestDto
    ) {
        return ApiResponseDto.success(SuccessStatus.CREATE_CATEGORY_SUCCESS,
            categoryService.createCategory(user.getUsername(), categoryRequestDto));
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponseDto<CategoryResponseDto> deleteCategory(
        @AuthenticationPrincipal User user,
        @PathVariable("categoryId") Long categoryId
    ) {
        return ApiResponseDto.success(SuccessStatus.DELETE_CATEGORY_SUCCESS,
            prayFacade.deleteCategory(user.getUsername(), categoryId));
    }

    @PutMapping("/{categoryId}")
    public ApiResponseDto<CategoryResponseDto> updatePray(
        @PathVariable("categoryId") Long categoryId,
        @RequestBody @Valid CategoryRequestDto categoryRequestDto,
        @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.UPDATE_CATEGORY_SUCCESS,
            categoryService.updateCategory(user.getUsername(), categoryId, categoryRequestDto));
    }


    @PutMapping("/{categoryId}/order/{index}")
    public ApiResponseDto<CategoryResponseDto> updatePrayOrder(
        @PathVariable("categoryId") Long categoryId,
        @PathVariable("index") int index,
        @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.UPDATE_CATEGORY_SUCCESS,
            categoryService.updateCategoryOrder(user.getUsername(), categoryId, index));
    }
}
