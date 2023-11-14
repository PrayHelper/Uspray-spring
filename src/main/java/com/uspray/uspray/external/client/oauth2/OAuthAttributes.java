package com.uspray.uspray.external.client.oauth2;

import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.external.client.oauth2.dto.GoogleOAuth2UserInfo;
import com.uspray.uspray.external.client.oauth2.dto.KakaoOAuth2UserInfo;
import com.uspray.uspray.external.client.oauth2.dto.OAuth2UserInfo;
import com.uspray.uspray.infrastructure.MemberRepository;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
public class OAuthAttributes {
    private final String nameAttributeKey;
    private final OAuth2UserInfo oAuth2UserInfo;
    private final MemberRepository memberRepository;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo,
        MemberRepository memberRepository) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
        this.memberRepository = memberRepository;
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

    public Member toEntity(OAuth2UserInfo oAuth2UserInfo) {
        return Member.builder()
            .name("Uspray")
            .userId(generateRandomId())
            .socialId(oAuth2UserInfo.getId())
            .authority(Authority.ROLE_USER)
            .build();
    }

    private String generateRandomId() {
        while (true) {
            String randomId = RandomStringUtils.random(15);
            if (!memberRepository.existsByUserId(randomId)) {
                return randomId;
            }
        }
    }
}
