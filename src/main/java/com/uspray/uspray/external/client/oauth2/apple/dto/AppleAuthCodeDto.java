package com.uspray.uspray.external.client.oauth2.apple.dto;

import lombok.Data;

@Data
public class AppleAuthCodeDto {

    String code;
    String id_token;

}
