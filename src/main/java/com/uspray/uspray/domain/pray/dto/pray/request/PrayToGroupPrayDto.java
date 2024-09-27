package com.uspray.uspray.domain.pray.dto.pray.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
public class PrayToGroupPrayDto {

    @Schema(example = "1")
    private Long groupId;
    private List<Long> prayId;

}
