package com.uspray.uspray.DTO.sms;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsRequestDto {

    private String type;
    private String contentType;
    private String countryCode;
    private String from;
    private String content;
    private List<MessageDto> messages;
}
