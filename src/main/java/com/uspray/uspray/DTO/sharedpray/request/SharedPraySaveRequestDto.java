package com.uspray.uspray.DTO.sharedpray.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "공유 기도제목 저장 요청 DTO")
public class SharedPraySaveRequestDto {

    @NotNull
    private List<Long> sharedPrayIds;

    @NotNull
    private Long categoryId;
}
