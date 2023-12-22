package com.uspray.uspray.DTO.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DupCheckResponseDto {

    @Schema(description = "중복 여부", example = "true")
    Boolean duplicate;
}
