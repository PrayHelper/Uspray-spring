package com.uspray.uspray.service;

import com.uspray.uspray.DTO.auth.request.OauthNameDto;
import com.uspray.uspray.DTO.notification.NotificationAgreeDto;
import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void changePhone(String userId, String phone) {
        memberRepository.getMemberByUserId(userId).changePhone(phone);
    }

    @Transactional
    public void changeNotificationAgree(String userId, NotificationAgreeDto notificationAgreeDto) {
        memberRepository.getMemberByUserId(userId).changeNotificationSetting(notificationAgreeDto);
    }

    @Transactional
    public void changeName(OauthNameDto oauthNameDto) {
        Member member = memberRepository.findBySocialId(oauthNameDto.getId())
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.NOT_FOUND_USER_EXCEPTION,
                ErrorStatus.NOT_FOUND_USER_EXCEPTION.getMessage()));
        member.changeName(oauthNameDto.getName());
        member.changeAuthority(Authority.ROLE_USER);
    }
}
