package com.uspray.uspray.DTO.history.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistorySearchRequestDto {

    @Schema(description = "검색 키워드", example = "기도")
    String keyword;
    @NotNull
    @Schema(description = "검색 시작 날짜", example = "2023-01-01")
    LocalDate startDate;
    @NotNull
    @Schema(description = "검색 종료 날짜", example = "2024-01-01")
    LocalDate endDate;
    @NotNull
    @Schema(description = "페이지", example = "0")
    Integer page;
    @NotNull
    @Schema(description = "페이지 크기", example = "10")
    Integer size;
    @Schema(description = "내가 쓴 기도제목만 검색", example = "true")
    Boolean isPersonal;
    @Schema(description = "공유받은 기도제목만 검색", example = "true")
    Boolean isShared;

}
