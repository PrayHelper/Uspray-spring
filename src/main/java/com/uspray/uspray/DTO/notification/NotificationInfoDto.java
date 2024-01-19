package com.uspray.uspray.DTO.notification;

import lombok.Builder;
import lombok.Data;

@Data
public class NotificationInfoDto {

    private Boolean firstNotiAgree;
    private Boolean secondNotiAgree;
    private Boolean thirdNotiAgree;

    @Builder
    public NotificationInfoDto(Boolean firstNotiAgree, Boolean secondNotiAgree,
        Boolean thirdNotiAgree) {
        this.firstNotiAgree = firstNotiAgree;
        this.secondNotiAgree = secondNotiAgree;
        this.thirdNotiAgree = thirdNotiAgree;
    }
}
