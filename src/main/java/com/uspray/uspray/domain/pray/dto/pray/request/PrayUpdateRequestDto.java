package com.uspray.uspray.domain.pray.dto.pray.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기도제목 수정 DTO")
public class PrayUpdateRequestDto {

    @Schema(description = "기도제목 내용", example = "@@이가 $$ 할 수 있도록")
    private String content;

    @Schema(description = "기도제목 마감일", example = "2025-01-01")
    private LocalDate deadline;

    @Schema(description = "기도제목 카테고리", example = "1")
    private Long categoryId;
}
