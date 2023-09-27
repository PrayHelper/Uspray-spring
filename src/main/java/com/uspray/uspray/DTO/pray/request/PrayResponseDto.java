package com.uspray.uspray.DTO.pray.request;

import com.uspray.uspray.domain.Pray;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  @Schema(description = "기도 횟수", example = "10")
  private Integer count;

  @Schema(description = "기도제목 생성일", example = "2021-01-01 00:00:00")
  private LocalDateTime createdAt;

  public static PrayResponseDto of(Pray pray) {
    return new PrayResponseDto(pray.getId(), pray.getContent(), pray.getDeadline(),
        pray.getCount(), pray.getCreatedAt());
  }

}
