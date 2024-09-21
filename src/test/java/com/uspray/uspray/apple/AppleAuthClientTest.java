package com.uspray.uspray.apple;

import com.uspray.uspray.global.external.client.oauth2.apple.AppleMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("set2")
public class AppleAuthClientTest {

    @Autowired
    AppleMemberService appleMemberService;

    @Test
    void getToken() {
        String authorizationCode = "ca4ca1fd98eda4ac28c8a6cc862135203.0.rrvrz.uNlZGtrCbQLgSwEO9CN2hg";

        var source = appleMemberService.login(authorizationCode);
    }
}
