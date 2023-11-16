package com.uspray.uspray.external.client.oauth2;

import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.external.client.oauth2.dto.GoogleOAuth2UserInfo;
import com.uspray.uspray.external.client.oauth2.dto.KakaoOAuth2UserInfo;
import com.uspray.uspray.external.client.oauth2.dto.OAuth2UserInfo;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
    private final String nameAttributeKey;
    private final OAuth2UserInfo oAuth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
        Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
        Map<String, Object> attributes) {
        return OAuthAttributes.builder()
            .nameAttributeKey(userNameAttributeName)
            .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
            .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName,
        Map<String, Object> attributes) {
        return OAuthAttributes.builder()
            .nameAttributeKey(userNameAttributeName)
            .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
            .build();
    }

    public Member toEntity(OAuth2UserInfo oAuth2UserInfo, String randomId) {
        return Member.builder()
            .name("Uspray")
            .userId(randomId)
            .socialId(oAuth2UserInfo.getId())
            .authority(Authority.ROLE_USER)
            .build();
    }

}
