package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class TestController {

    @GetMapping("/test")
    public ApiResponseDto<String> test() {
        return ApiResponseDto.success(SuccessStatus.TEST_SUCCESS,
            SuccessStatus.TEST_SUCCESS.getMessage());
    }
}