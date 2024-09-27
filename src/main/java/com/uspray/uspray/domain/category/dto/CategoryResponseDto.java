package com.uspray.uspray.domain.category.dto;

import com.uspray.uspray.domain.category.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CategoryResponseDto {

    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 이름", example = "카테고리 이름")
    private String name;

    @Schema(description = "카테고리 색상", example = "#FFFFFF")
    private String color;

    @Schema(description = "카테고리 순서", example = "1")
    private Integer order;

    public static CategoryResponseDto of(Category category) {
        return new CategoryResponseDto(category.getId(),
            category.getName(), category.getColor(), category.getOrder());
    }
}
