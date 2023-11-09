package com.uspray.uspray.DTO.pray;

import com.uspray.uspray.DTO.pray.request.PrayResponseDto;
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
  private List<PrayResponseDto> prays;
}