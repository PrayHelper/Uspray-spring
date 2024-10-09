package com.uspray.uspray.domain.pray.service;

import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
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

    public PrayResponseDto savePray(Pray pray) {
        return PrayResponseDto.of(prayRepository.save(pray));
    }
}
