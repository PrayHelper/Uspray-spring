package com.uspray.uspray.external.client.oauth2.apple;

import java.util.Base64;

public class TokenDecoder {

    public static <T> String decodePayload(String token, Class<T> targetClass) {

        String[] tokenParts = token.split("\\.");
        String payloadJWT = tokenParts[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));
        return payload;

//        ObjectMapper objectMapper = new ObjectMapper()
//            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        try {
//            return objectMapper.readValue(payload, targetClass);
//        } catch (Exception e) {
//            throw new RuntimeException("Error decoding token payload", e);
//        }
    }
}
