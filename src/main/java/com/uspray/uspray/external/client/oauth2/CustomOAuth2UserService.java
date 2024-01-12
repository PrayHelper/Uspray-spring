package com.uspray.uspray.external.client.oauth2;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.MemberRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    /**
     * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
     * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
     * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
     * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest); // OAuth2 정보를 가져옵니다.

        //현재 로그인 진행 중인 서비스를 구분하는 코드 (구글 or 네이버 or 카카오 ...)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        //OAuth2 로그인 진행 시 키가되는 필드 값, Primary Key와 같은 의미
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
            .getUserInfoEndpoint().getUserNameAttributeName();

        //OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
        // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
            oAuth2User.getAttributes());

        Member member = getMember(attributes);

        return new CustomOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().name())),
            oAuth2User.getAttributes(),
            attributes.getNameAttributeKey(),
            member.getAuthority(),
            attributes.getOAuth2UserInfo().getId()
        );
    }

    private Member getMember(OAuthAttributes attributes) {
        Member findMember = memberRepository.findBySocialId(
            attributes.getOAuth2UserInfo().getId()).orElse(null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) { //소셜 로그인 연동
            return saveMember(attributes, authentication.getName());
        }

        if (findMember == null) {
            return saveMember(attributes);
        }

        return findMember;
    }

    /**
     * 이미 존재하는 회원이라면 이름과 프로필이미지를 업데이트해줍니다.
     * 처음 가입하는 회원이라면 Member 테이블을 생성합니다. (소셜 회원가입)
     **/
    private Member saveMember(OAuthAttributes attributes, String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        member.changeSocialId(attributes.getOAuth2UserInfo().getId());
        return memberRepository.save(member);
    }

    //처음 가입
    private Member saveMember(OAuthAttributes attributes) {
        Member member = attributes.toEntity(attributes.getOAuth2UserInfo(), generateRandomId());
        return memberRepository.save(member);
    }

    private String generateRandomId() {
        while (true) {
            String randomId = RandomStringUtils.random(15, true, true);
            if (!memberRepository.existsByUserId(randomId)) {
                return randomId;
            }
        }
    }

}
