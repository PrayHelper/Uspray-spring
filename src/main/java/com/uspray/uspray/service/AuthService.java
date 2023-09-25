package com.uspray.uspray.service;

import com.uspray.uspray.DTO.auth.TokenDto;
import com.uspray.uspray.DTO.auth.request.FindIdDto;
import com.uspray.uspray.DTO.auth.request.FindPwDto;
import com.uspray.uspray.DTO.auth.request.MemberLoginRequestDto;
import com.uspray.uspray.DTO.auth.request.MemberRequestDto;
import com.uspray.uspray.DTO.auth.request.TokenRequestDto;
import com.uspray.uspray.DTO.auth.response.MemberResponseDto;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.ExistIdException;
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
        if (memberRepository.existsByUserId(memberRequestDto.getUserId())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        Member member = memberRequestDto.toMember(passwordEncoder);
        return MemberResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public TokenDto login(MemberLoginRequestDto memberLoginRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = memberLoginRequestDto.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                tokenDto.getRefreshToken(),
                tokenProvider.getRefreshTokenExpireTime(),
                TimeUnit.MILLISECONDS);
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        if (!tokenProvider.validateToken(tokenRequestDto.getAccessToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다");
        }
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
        String refreshTokenValue = redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if (!tokenRequestDto.getRefreshToken().equals(refreshTokenValue)) {
            throw new RuntimeException("Refresh Token 이 일치하지 않습니다");
        }
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                tokenDto.getRefreshToken(),
                tokenProvider.getRefreshTokenExpireTime(),
                TimeUnit.MILLISECONDS);
        return tokenDto;
    }


    //Custom exception merge된 후 예외처리 하기
    public String findId(FindIdDto findIdDto) {
        return memberRepository.findByNameAndPhone(findIdDto.getName(), findIdDto.getPhone()).getUserId();
    }

    @Transactional
    public void findPw(FindPwDto findPwDto) {
        memberRepository.findByNameAndPhoneAndUserId(
            findPwDto.getName(), findPwDto.getPhone(),
            findPwDto.getUserId()).changePw(passwordEncoder.encode(findPwDto.getPassword()));
    }

    @Transactional
    public void withdrawal(String userId) {
        memberRepository.delete(memberRepository.getMemberByUserId(userId));
    }

    public void dupCheck(String userId) {

        if (memberRepository.existsByUserId(userId)) {
            throw new ExistIdException(ErrorStatus.ALREADY_EXIST_ID_EXCEPTION,
                ErrorStatus.ALREADY_EXIST_ID_EXCEPTION.getMessage());
        }
    }


}
