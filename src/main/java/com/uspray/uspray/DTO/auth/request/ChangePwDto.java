package com.uspray.uspray.DTO.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChangePwDto {

    @Schema(example = "1")
    Long id;

    @Schema(example = "password")
    String password;

}
