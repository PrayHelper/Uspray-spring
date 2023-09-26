package com.uspray.uspray.DTO.sms;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SmsResponseDto {

    private String requestId;
    private LocalDateTime requestTime;
    private String statusCode;
    private String statusName;
}
