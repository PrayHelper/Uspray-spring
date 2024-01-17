package com.uspray.uspray.DTO.notification;

import lombok.Builder;
import lombok.Data;

@Data
public class NotificationInfoDto {

    private Boolean firstNotiAgree;
    private Boolean secondNotiAgree;
    private Boolean thirdNotiAgree;
    private Boolean fourthNotiAgree;

    @Builder
    public NotificationInfoDto(Boolean firstNotiAgree, Boolean secondNotiAgree, Boolean thirdNotiAgree, Boolean fourthNotiAgree) {
        this.firstNotiAgree = firstNotiAgree;
        this.secondNotiAgree = secondNotiAgree;
        this.thirdNotiAgree = thirdNotiAgree;
        this.fourthNotiAgree = fourthNotiAgree;
    }
}
