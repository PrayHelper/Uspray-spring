package com.uspray.uspray.domain.pray.dto.pray;

import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PrayListResponseDto {

    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;
    @Schema(description = "카테고리 이름", example = "카테고리 이름")
    private String categoryName;
    @Schema(description = "카테고리 색상", example = "#75BD62")
    private String categoryColor;
    private List<PrayResponseDto> prays;

}
