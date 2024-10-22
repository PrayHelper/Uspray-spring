package com.uspray.uspray.domain.pray.service;

import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
import java.util.List;
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

    public Pray findPrayById(Long prayId) {
        return prayRepository.getPrayById(prayId);
    }

    public List<Pray> findAllByIdIn(List<Long> prayIds) {
        return prayRepository.findAllByIdIn(prayIds);
    }

    public List<Pray> findAllByOriginPrayIdIn(List<Long> prayIds) {
        return prayRepository.findAllByOriginPrayIdIn(prayIds);
    }
}
