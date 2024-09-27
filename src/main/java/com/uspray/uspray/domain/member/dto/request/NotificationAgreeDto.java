package com.uspray.uspray.domain.member.dto.request;

import com.uspray.uspray.global.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NotificationAgreeDto {

    @Schema(example = "2")
    private NotificationType notificationType;
    @Schema(example = "false")
    private Boolean agree;

}
