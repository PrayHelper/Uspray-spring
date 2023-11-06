package com.uspray.uspray.DTO.history.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "기도제목 검색 DTO")
public class HistorySearchRequestDto {

    private String keyword;

    private Boolean isMine;

    private Boolean isShared;

    private LocalDate startDate;

    private LocalDate endDate;
}
