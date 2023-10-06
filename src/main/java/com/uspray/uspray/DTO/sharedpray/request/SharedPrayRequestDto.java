package com.uspray.uspray.DTO.sharedpray.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "기도제목 공유 요청 DTO")
public class SharedPrayRequestDto {

    @NotNull
    private Long prayId;

    @NotNull
    private String receiverId;
}
