package com.uspray.uspray.DTO.pray.request;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
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

  public Pray toPray(Member member) {
    return Pray.builder()
        .content(content)
        .deadline(deadline)
        .member(member)
        .build();
  }

  public Pray toEntity(Member member) {
    return Pray.builder()
        .content(content)
        .deadline(deadline)
        .member(member)
        .build();
  }
}
