package com.uspray.uspray.DTO.category;

import com.uspray.uspray.Enums.CategoryType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 DTO")
public class CategoryRequestDto {

    @Length(min = 1, max = 20)
    @Schema(description = "카테고리 이름", example = "카테고리 이름")
    private String name;

    @Schema(description = "카테고리 색상", example = "#75BD62")
    private String color;

    @Schema(description = "카테고리 타입", example = "personal")
    private String type;

    public Category toEntity(Member member, Integer order) {
        CategoryType convertCategoryType = CategoryType.valueOf(type.toUpperCase());
        return Category.builder()
            .name(name)
            .color(color)
            .member(member)
            .order(order)
            .categoryType(convertCategoryType)
            .build();
    }

}
