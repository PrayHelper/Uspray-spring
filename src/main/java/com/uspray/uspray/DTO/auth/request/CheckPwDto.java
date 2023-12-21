package com.uspray.uspray.DTO.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CheckPwDto {
    @Schema(example = "test")
    String userId;
    @Schema(example = "01012345678")
    String phone;

}
