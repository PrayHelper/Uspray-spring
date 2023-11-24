package com.uspray.uspray.DTO.pray.response;

import com.uspray.uspray.domain.Pray;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "기도제목 응답 DTO")
public class PrayResponseDto {

    @Schema(description = "기도제목 id", example = "3")
    private Long prayId;

    @NotNull
    @Schema(description = "기도제목 내용", example = "@@이가 $$ 할 수 있도록")
    private String content;

    @NotNull
    @Schema(description = "기도제목 마감일", example = "2025-01-01")
    private LocalDate deadline;

    @Schema(description = "기도제목 카테고리", example = "1")
    private Long categoryId;

    @Schema(description = "기도제목 카테고리 이름", example = "카테고리 이름")
    private String categoryName;

    @Schema(description = "기도제목 마지막 기도일, example = 2021-01-01")
    private LocalDate lastPrayDate;


    public static PrayResponseDto of(Pray pray) {
        return new PrayResponseDto(pray.getId(), pray.getContent(), pray.getDeadline(),
            pray.getCategory().getId(),
            pray.getCategory().getName(),
            pray.getLastPrayedAt());
    }

}
