package com.uspray.uspray.service;

import com.uspray.uspray.controller.dto.TokenDto;
import com.uspray.uspray.controller.dto.request.MemberLoginRequestDto;
import com.uspray.uspray.controller.dto.request.MemberRequestDto;
import com.uspray.uspray.controller.dto.response.MemberResponseDto;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public MemberResponseDto signup(MemberRequestDto memberRequestDto) {
        // 핸드폰번호가 존재하거나 아이디가 존재하면 에러
        if (memberRepository.existsByPhoneNum(memberRequestDto.getPhoneNum()) || memberRepository.existsByUserId(memberRequestDto.getUserId())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        Member member = memberRequestDto.toMember(passwordEncoder);
        return MemberResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public TokenDto login(MemberLoginRequestDto memberLoginRequestDto) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = memberLoginRequestDto.toAuthentication();
        System.out.println("authToken: " + authenticationToken);

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        System.out.println("auth: " + authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        System.out.println("tokenDto: " + tokenDto);

        // 4. RefreshToken 저장
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                tokenDto.getRefreshToken(),
                tokenProvider.getRefreshTokenExpireTime(),
                TimeUnit.MILLISECONDS);
        System.out.println("redis");
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(String accessToken, String refreshToken) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(accessToken);

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져오기
        String refreshTokenValue = redisTemplate.opsForValue().get("RT:" + authentication.getName()).toString();

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.equals(refreshTokenValue)) {
            throw new RuntimeException("Refresh Token 이 일치하지 않습니다");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                tokenDto.getRefreshToken(),
                tokenProvider.getRefreshTokenExpireTime(),
                TimeUnit.MILLISECONDS);

        // 토큰 발급
        return tokenDto;
    }
}
