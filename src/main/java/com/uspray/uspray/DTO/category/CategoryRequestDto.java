package com.uspray.uspray.DTO.category;

import com.uspray.uspray.Enums.CategoryType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 DTO")
public class CategoryRequestDto {

    @NotNull
    @Schema(description = "카테고리 이름", example = "카테고리 이름")
    private String name;

    @NotNull
    @Schema(description = "카테고리 색상", example = "#75BD62")
    private String color;

    @NotNull
    @Schema(description = "카테고리 타입", example = "PERSONAL")
    private CategoryType type;

    public Category toEntity(Member member, Integer order) {
        return Category.builder()
            .name(name)
            .color(color)
            .member(member)
            .order(order)
            .categoryType(type)
            .build();
    }

}
