package com.uspray.uspray.external.client.oauth2.apple;

import com.uspray.uspray.DTO.auth.TokenDto;
import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.external.client.oauth2.apple.dto.AppleIdTokenPayload;
import com.uspray.uspray.external.client.oauth2.apple.dto.AppleLoginResponseDto;
import com.uspray.uspray.external.client.oauth2.apple.dto.AppleProperties;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.jwt.TokenProvider;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.PrivateKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleMemberService {

    private final AppleAuthClient appleAuthClient;
    private final AppleProperties appleProperties;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public AppleLoginResponseDto login(String authorizationCode) {

        //apple accessToken
        String idToken = appleAuthClient.getIdToken(
            appleProperties.getClientId(),
            generateClientSecret(),
            appleProperties.getGrantType(),
            authorizationCode
        ).getIdToken();

        AppleIdTokenPayload payload = TokenDecoder.decodePayload(idToken,
            AppleIdTokenPayload.class);
        Member findMember = memberRepository.findBySocialId(payload.getSub()).orElse(null);
        Member member = findMember;

        if (findMember == null) {
            member = Member.builder()
                .socialId(payload.getSub())
                .authority(Authority.ROLE_GUEST)
                .build();
            memberRepository.save(member);
            log.info("애플 회원가입에 성공하였습니다.");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(
            new UsernamePasswordAuthenticationToken(member.getSocialId(), null, null));
        return AppleLoginResponseDto.of(member.getName(), member.getSocialId(), tokenDto);
    }

    private String generateClientSecret() {

        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

        return Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, appleProperties.getKeyId())
            .setIssuer(appleProperties.getTeamId())
            .setAudience(appleProperties.getAudience())
            .setSubject(appleProperties.getClientId())
            .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
            .setIssuedAt(new Date())
            .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
            .compact();
    }

    private PrivateKey getPrivateKey() {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(appleProperties.getPrivateKey());

            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
            return converter.getPrivateKey(privateKeyInfo);
        } catch (Exception e) {
            throw new RuntimeException("Error converting private key from String", e);
        }
    }
}