package com.uspray.uspray.external.client.oauth2;

import com.uspray.uspray.DTO.auth.TokenDto;
import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.jwt.TokenProvider;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String id = customOAuth2User.getSocialId();

        // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
        if(customOAuth2User.getAuthority() == Authority.ROLE_GUEST) {
            String accessToken = tokenProvider.generateTokenDto(authentication).getAccessToken();
            response.sendRedirect("https://www.uspray.kr/socialLoginNameInput"+"?token="+accessToken+"&id="+id); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트 (추가 컨트롤러를 만들고 거기서 post 해야지 User로 바뀜)
            return;
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
            tokenDto.getRefreshToken(),
            tokenProvider.getRefreshTokenExpireTime(),
            TimeUnit.MILLISECONDS);

        response.sendRedirect("https://www.uspray.kr/social-redirecting"+"?accessToken="+tokenDto.getAccessToken()+"&refreshToken="+tokenDto.getRefreshToken());
    }
}
