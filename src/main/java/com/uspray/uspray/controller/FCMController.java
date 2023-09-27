package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.notification.FCMNotificationRequestDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.FCMNotificationService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FCMController {

    private final FCMNotificationService fcmNotificationService;

    @PostMapping("/admin/send/push")
    public ApiResponseDto<?> pushMessage(@RequestBody FCMNotificationRequestDto requestDto) throws IOException {

        fcmNotificationService.sendMessageTo(
            requestDto.getToken(),
            requestDto.getTitle(),
            requestDto.getBody());
        return ApiResponseDto.success(SuccessStatus.PUSH_SUCCESS);
    }

}
