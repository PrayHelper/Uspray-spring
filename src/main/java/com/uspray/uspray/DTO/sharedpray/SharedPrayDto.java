package com.uspray.uspray.DTO.sharedpray;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedPrayDto {

    @Schema(description = "기도제목 ID 리스트", example = "1, 2, 3")
    private List<Long> prayIds;
}
