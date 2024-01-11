package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.infrastructure.PrayRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayService {

    private final PrayRepository prayRepository;

    @Transactional
    public PrayResponseDto getPrayDetail(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        return PrayResponseDto.of(pray);
    }
}
