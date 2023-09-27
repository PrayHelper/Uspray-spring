package com.uspray.uspray.service;

import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private MemberRepository memberRepository;

    @Transactional
    public void changePhone(String userId, String phone) {
        memberRepository.getMemberByUserId(userId).changePhone(phone);
    }
}
