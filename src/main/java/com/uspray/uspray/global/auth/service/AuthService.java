package com.uspray.uspray.global.auth.service;

import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.member.repository.MemberRepository;
import com.uspray.uspray.global.auth.dto.member.TokenDto;
import com.uspray.uspray.global.auth.model.Withdraw;
import com.uspray.uspray.global.auth.repository.WithdrawRepository;
import com.uspray.uspray.global.enums.WithdrawReason;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.global.jwt.TokenProvider;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final WithdrawRepository withdrawRepository;

    @Transactional
    public com.uspray.uspray.DTO.auth.response.MemberResponseDto signup(
        com.uspray.uspray.DTO.auth.request.MemberRequestDto memberRequestDto) {
        // 핸드폰번호가 존재하거나 아이디가 존재하면 에러
        // 핸드폰 번호 또는 아이디가 이미 존재하는지 확인
        if (memberRepository.existsByUserId(memberRequestDto.getUserId())) {
            throw new NotFoundException(ErrorStatus.ALREADY_EXIST_ID_EXCEPTION);
        }
        if (memberRepository.existsByPhone(memberRequestDto.getPhone())) {
            throw new NotFoundException(ErrorStatus.ALREADY_EXIST_PHONE_EXCEPTION);
        }

        Member member = memberRequestDto.toMember(passwordEncoder);
        return com.uspray.uspray.DTO.auth.response.MemberResponseDto.of(memberRepository.save(member));
    }


    @Transactional
    public TokenDto login(
        com.uspray.uspray.DTO.auth.request.MemberLoginRequestDto memberLoginRequestDto) {
        try {
            // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken = memberLoginRequestDto.toAuthentication();

            // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
            //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
            Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            // 4. RefreshToken 저장
            redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                tokenDto.getRefreshToken(),
                tokenProvider.getRefreshTokenExpireTime(),
                TimeUnit.MILLISECONDS);

            return tokenDto;
        } catch (AuthenticationException e) {
            throw new NotFoundException(ErrorStatus.WRONG_LOGIN_INFO_EXCEPTION);
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public TokenDto reissue(String refreshToken) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new NotFoundException(ErrorStatus.REFRESH_TOKEN_NOT_VALID_EXCEPTION);
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져오기
        String refreshTokenValue = redisTemplate.opsForValue()
            .get("RT:" + authentication.getName());

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.equals(refreshTokenValue)) {
            throw new NotFoundException(ErrorStatus.REFRESH_TOKEN_NOT_VALID_EXCEPTION);
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

    //Custom exception merge된 후 예외처리 하기
    public String findId(com.uspray.uspray.DTO.auth.request.FindIdDto findIdDto) {
        return memberRepository.findByNameAndPhone(findIdDto.getName(), findIdDto.getPhone())
            .getUserId();
    }


    public Long findPw(com.uspray.uspray.DTO.auth.request.FindPwDTO findPwDTO) {
        return memberRepository.findByUserIdAndPhone(
            findPwDTO.getUserId(), findPwDTO.getPhone()).getId();
    }

    @Transactional
    public void changePw(com.uspray.uspray.DTO.auth.request.ChangePwDto changePwDto) {
        memberRepository.getMemberById(changePwDto.getId())
            .changePw(passwordEncoder.encode(changePwDto.getPassword()));
    }

    @Transactional
    public void withdrawal(String userId, com.uspray.uspray.DTO.auth.request.MemberDeleteDto memberDeleteDto) {
        Member member = memberRepository.getMemberByUserId(userId);
        for (WithdrawReason withdrawReason : memberDeleteDto.getWithdrawReason()) {
            if (Objects.equals(withdrawReason, WithdrawReason.ETC)) {
                withdrawRepository.save(Withdraw.builder()
                    .memberId(member.getId())
                    .withdrawReason(withdrawReason)
                    .description(memberDeleteDto.getDescription())
                    .build());
            }
            withdrawRepository.save(Withdraw.builder()
                .memberId(member.getId())
                .withdrawReason(withdrawReason)
                .build());
        }
        memberRepository.delete(member);
    }

    public com.uspray.uspray.DTO.auth.response.MemberNameResponseDto getMemberName(String userId) {
        return new com.uspray.uspray.DTO.auth.response.MemberNameResponseDto(memberRepository.getMemberByUserId(userId).getName());
    }

    public com.uspray.uspray.DTO.auth.response.DupCheckResponseDto dupCheck(String userId) {
        return new com.uspray.uspray.DTO.auth.response.DupCheckResponseDto(memberRepository.existsByUserId(userId));
    }

    public com.uspray.uspray.DTO.auth.response.LoginTypeResponseDto loginCheck(String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        return com.uspray.uspray.DTO.auth.response.LoginTypeResponseDto.builder()
            .isSocial(member.getSocialId() != null)
            .build();
    }
}
