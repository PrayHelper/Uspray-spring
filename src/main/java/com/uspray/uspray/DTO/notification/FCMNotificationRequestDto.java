package com.uspray.uspray.DTO.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FCMNotificationRequestDto {

    @Schema(example = "device token")
    private String token;
    @Schema(example = "오펜하이머")
    private String title;
    @Schema(example = "나는 곧 쭈꾸미오")
    private String body;

}
