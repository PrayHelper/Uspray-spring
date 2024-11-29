package com.uspray.uspray.domain.pray.dto.pray.request;

import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.global.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "기도제목 DTO")
public class PrayRequestDto {

	@NotNull
	@Schema(description = "기도제목 내용", example = "@@이가 $$ 할 수 있도록")
	private String content;

	@NotNull
	@Schema(description = "기도제목 마감일", example = "2025-01-01")
	private LocalDate deadline;

	@NotNull
	@Schema(description = "기도제목 카테고리", example = "1")
	private Long categoryId;

	public Pray toEntity(Member member, Category category, LocalDate startDateOrNull) {
		return Pray.builder()
			.content(content)
			.deadline(deadline)
			.member(member)
			.category(category)
			.categoryType(CategoryType.PERSONAL)
			.startDate(startDateOrNull)
			.build();
	}
}
