package com.uspray.uspray.service;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.dto.pray.PrayDto;
import com.uspray.uspray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PrayService {
    private final PrayRepository prayRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PrayDto createPray(PrayRequestDto prayRequestDto) {
        Member member = memberRepository.getMemberByName("user");
        System.out.println(member);
        Pray pray = prayRequestDto.toEntity(member);
//        prayRepository.save(pray);
        return PrayDto.builder()
                .id(1L)
                .memberId(1L)
                .content(pray.getContent())
                .count(pray.getCount())
                .deadline(pray.getDeadline())
                .build();
    }
}
