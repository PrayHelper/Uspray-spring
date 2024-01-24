package com.uspray.uspray.external.client.oauth2.apple;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apple")
public class AppleController {

    private final GetMemberInfoService getMemberInfoService;

    @PostMapping("/token")
    public ApiResponseDto<?> getAppleToken(@RequestBody String authCode) {
        return ApiResponseDto.success(SuccessStatus.TEST_SUCCESS, getMemberInfoService.get(authCode));
    }


}
