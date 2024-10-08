package com.uspray.uspray.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OauthNameDto {

    @Schema(example = "DQWGRDDSdsadqwdweqwsd")
    private String id;
    @Schema(example = "춥다")
    private String name;

}
