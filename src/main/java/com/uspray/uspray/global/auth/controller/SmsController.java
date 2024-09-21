package com.uspray.uspray.global.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uspray.uspray.global.common.dto.ApiResponseDto;
import com.uspray.uspray.global.auth.dto.sms.CertificationDto;
import com.uspray.uspray.global.auth.dto.sms.MessageDto;
import com.uspray.uspray.global.auth.dto.sms.SmsResponseDto;
import com.uspray.uspray.global.exception.SuccessStatus;
import com.uspray.uspray.global.auth.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "sms 전송", description = "SMS 관련 API")
public class SmsController {

  private final SmsService smsService;

  @PostMapping("/sms/send")
  @Operation(summary = "sms 전송")
  public ApiResponseDto<SmsResponseDto> sendSms(@RequestBody MessageDto messageDto)
      throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    return ApiResponseDto.success(SuccessStatus.SEND_SMS_SUCCESS, smsService.sendSms(messageDto));
  }

  @PostMapping("/sms/verification")
  @Operation(summary = "인증 코드 확인")
  public ApiResponseDto<?> sendSms(@RequestBody CertificationDto certificationDto) {
    smsService.getCertification(certificationDto);
    return ApiResponseDto.success(SuccessStatus.CERTIFICATION_SUCCESS);
  }
}