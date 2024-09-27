package com.uspray.uspray.global.external.client.oauth2;

import com.uspray.uspray.global.enums.Authority;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.external.client.oauth2.dto.KakaoOAuth2UserInfo;
import com.uspray.uspray.global.external.client.oauth2.dto.NaverOAuth2UserInfo;
import com.uspray.uspray.global.external.client.oauth2.dto.OAuth2UserInfo;
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
        if ("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes);
        }
        return ofKakao(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName,
        Map<String, Object> attributes) {
        return OAuthAttributes.builder()
            .nameAttributeKey(userNameAttributeName)
            .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
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
            .password("uspray")
            .phone("010-0000-0000")
            .socialId(oAuth2UserInfo.getId())
            .authority(Authority.ROLE_GUEST)
            .build();
    }

}
