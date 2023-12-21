package com.uspray.uspray.DTO.history.request;

import lombok.Data;

@Data
public class HistoryRequestDto {

    private String type = "personal";

    private Integer page = 0;

    private Integer size = 10;

}
