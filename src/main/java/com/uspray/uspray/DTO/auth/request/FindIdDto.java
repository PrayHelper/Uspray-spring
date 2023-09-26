package com.uspray.uspray.DTO.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FindIdDto {

    @Schema(example = "이름")
    private String name;
    @Schema(example = "01048896257")
    private String phone;

}
