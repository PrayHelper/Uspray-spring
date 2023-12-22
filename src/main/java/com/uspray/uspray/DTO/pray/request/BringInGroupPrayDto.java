package com.uspray.uspray.DTO.pray.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
public class BringInGroupPrayDto {

    @Schema(example = "1")
    private Long groupId;
    private List<Long> prayId;

}
