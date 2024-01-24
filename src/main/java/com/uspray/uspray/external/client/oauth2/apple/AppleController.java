package com.uspray.uspray.external.client.oauth2.apple;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppleController {

    @PostMapping("/login/oauth2/code/apple")
    public ResponseEntity<MsgEntity> callback(HttpServletRequest request) throws Exception {
//        AppleDTO appleInfo = customRequestEntityConverter.getAppleInfo(request.getParameter("code"));
        System.out.println("::working test::");

        return ResponseEntity.ok()
            .body(new MsgEntity("Success", "Test"));
    }
}
