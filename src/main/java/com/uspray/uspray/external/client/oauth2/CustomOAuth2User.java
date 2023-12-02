package com.uspray.uspray.external.client.oauth2;

import com.uspray.uspray.Enums.Authority;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private final Authority authority;

    public CustomOAuth2User(
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes, String nameAttributeKey,
        Authority authority) {
        super(authorities, attributes, nameAttributeKey);
        this.authority = authority;
    }
}
