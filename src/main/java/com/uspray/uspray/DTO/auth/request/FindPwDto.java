package com.uspray.uspray.DTO.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FindPwDto {
    @Schema(example = "test")
    String userId;
    @Schema(example = "이름")
    String name;
    @Schema(example = "01064519986")
    String phone;
    @Schema(example = "password")
    String password;

}
