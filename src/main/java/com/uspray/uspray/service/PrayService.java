package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayResponseDto;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayService {

  private final PrayRepository prayRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username) {
    Member member = memberRepository.getMemberByUserId(username);
    System.out.println(member);
    Pray pray = prayRequestDto.toEntity(member);
    prayRepository.save(pray);
    return PrayResponseDto.of(pray);
  }
}
