package com.uspray.uspray.domain.pray.dto.sharedpray.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기도제목 공유 요청 DTO")
public class SharedPrayRequestDto {

    @NotNull
    private List<Long> prayIds;
}
