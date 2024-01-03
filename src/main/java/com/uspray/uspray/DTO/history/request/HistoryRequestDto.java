package com.uspray.uspray.DTO.history.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HistoryRequestDto {

    @Schema(description = "기도제목 타입", example = "personal")
    private String type = "personal";

    @Schema(description = "페이지 번호", example = "0")
    private Integer page = 0;

    @Schema(description = "페이지 사이즈", example = "10")
    private Integer size = 10;

}
