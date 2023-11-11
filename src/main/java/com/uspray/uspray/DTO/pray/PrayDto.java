package com.uspray.uspray.DTO.pray;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PrayDto {
    
    @Schema(description = "기도제목 ID", example = "1")
    private Long id;
    @Schema(description = "기도제목 작성자 ID", example = "1")
    private Long memberId;
    @Schema(description = "기도제목 내용", example = "@@이가 $$ 할 수 있도록")
    private String content;
    @Schema(description = "기도제목 마감일", example = "2025-01-01")
    private LocalDate deadline;
    @Schema(description = "기도 횟수", example = "10")
    private Integer count;
    @Schema(description = "기도제목 공유 여부", example = "true")
    private Boolean isShared;
    @Schema(description = "기도제목 종류", example = "PERSONAL")
    private String prayType;
    @Schema(description = "기도제목 마지막 기도일, example = 2021-01-01")
    private LocalDate lastPrayDate;
    @Schema(description = "기도제목 생성일", example = "2021-01-01 00:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "기도제목 수정일", example = "2021-01-01 00:00:00")
    private LocalDateTime updatedAt;
    
}

