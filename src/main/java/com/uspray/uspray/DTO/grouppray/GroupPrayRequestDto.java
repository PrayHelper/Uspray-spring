package com.uspray.uspray.DTO.grouppray;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupPrayRequestDto {

    @NotNull
    @Schema(description = "모임 Id", example = "1")
    private Long groupId;

    @NotNull
    @Schema(description = "기도제목 내영", example = "내")
    private String content;

    @NotNull
    @Schema(description = "기도제목 마감일", example = "2025-01-01")
    private LocalDate deadline;

    @NotNull
    @Schema(description = "기도제목 카테고리", example = "1")
    private Long categoryId;

}
