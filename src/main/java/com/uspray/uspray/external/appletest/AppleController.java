package com.uspray.uspray.external.appletest;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AppleController {
    @PostMapping("/login/oauth2/code/apple")
    public ResponseEntity<MsgEntity> callback(HttpServletRequest request) throws Exception {
        log.info(request.getParameter("code"));

        return ResponseEntity.ok()
            .body(new MsgEntity("Success", request.getParameter("code")));
    }
}
