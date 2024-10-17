package com.uspray.uspray.domain.member.service;

import com.uspray.uspray.domain.member.dto.request.CheckPwDTO;
import com.uspray.uspray.domain.member.dto.request.FcmTokenDto;
import com.uspray.uspray.domain.member.dto.request.OauthNameDto;
import com.uspray.uspray.domain.member.dto.request.NotificationAgreeDto;
import com.uspray.uspray.domain.member.dto.response.NotificationInfoDto;
import com.uspray.uspray.global.enums.Authority;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePhone(String userId, String phone) {
        if (memberRepository.existsByPhone(phone)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_PHONE_EXCEPTION);
        }
        memberRepository.getMemberByUserId(userId).changePhone(phone);
    }

    @Transactional(readOnly = true)
    public NotificationInfoDto getNotificationAgree(String userId) {
        return memberRepository.getMemberByUserId(userId).getNotificationSetting();
    }

    @Transactional
    public void changeNotificationAgree(String userId, NotificationAgreeDto notificationAgreeDto) {
        memberRepository.getMemberByUserId(userId).changeNotificationSetting(notificationAgreeDto);
    }

    @Transactional
    public void changeName(OauthNameDto oauthNameDto) {
        Member member = memberRepository.findBySocialId(oauthNameDto.getId())
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.NOT_FOUND_USER_EXCEPTION)
            );
        member.changeName(oauthNameDto.getName());
        member.changeAuthority(Authority.ROLE_USER);
    }

    public Boolean checkPw(String userId, CheckPwDTO checkPwDto) {
        Member member = memberRepository.getMemberByUserId(userId);
        if (!passwordEncoder.matches(checkPwDto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorStatus.NOT_FOUND_USER_EXCEPTION);
        }
        return true;
    }

    @Transactional
    public void changePw(String userId, CheckPwDTO changePwDto) {
        memberRepository.getMemberByUserId(userId)
            .changePw(passwordEncoder.encode(changePwDto.getPassword()));
    }

    @Transactional
    public void updateFcmToken(String username, FcmTokenDto fcmTokenDto) {
        memberRepository.getMemberByUserId(username).updateFcmToken(fcmTokenDto.getFcmToken());
    }

    public Member findMemberByUserId(String userId) {
        return memberRepository.getMemberByUserId(userId);
    }

    public Member findMemberById(Long id) {
        return memberRepository.getMemberById(id);
    }
}
