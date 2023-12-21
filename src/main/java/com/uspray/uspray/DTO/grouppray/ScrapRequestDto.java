package com.uspray.uspray.DTO.grouppray;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScrapRequestDto {

    @Schema(example = "1")
    private Long groupPrayId;
    @Schema(example = "1")
    private Long categoryId;

    @NotNull
    @Schema(description = "기도제목 마감일", example = "2025-01-01")
    private LocalDate deadline;

}
