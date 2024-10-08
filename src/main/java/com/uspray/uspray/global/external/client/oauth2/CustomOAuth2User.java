package com.uspray.uspray.global.external.client.oauth2;

import com.uspray.uspray.global.enums.Authority;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private final Authority authority;
    private final String socialId;
    private final String userId;

    public CustomOAuth2User(
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes, String nameAttributeKey,
        Authority authority, String socialId, String userId) {
        super(authorities, attributes, nameAttributeKey);
        this.authority = authority;
        this.socialId = socialId;
        this.userId = userId;
    }
}
