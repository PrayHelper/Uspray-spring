package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.infrastructure.PrayRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    @Transactional
    public List<PrayListResponseDto> getPrayList(String username, String prayType) {

        List<Pray> prays = prayRepository.findAllWithOrderAndType(username, prayType);

        // Pray 엔티티를 categoryId를 기준으로 그룹화한 맵 생성
        Map<Long, List<Pray>> prayMap = prays.stream()
            .collect(Collectors.groupingBy(pray -> pray.getCategory().getId()));

        if (prayType.equalsIgnoreCase(PrayType.PERSONAL.stringValue())) {
            // Pray 엔티티를 categoryId를 기준으로 그룹화한 맵을 PrayListResponseDto 변환하여 반환
            return prayMap.entrySet().stream()
                .map(entry -> new PrayListResponseDto(entry.getKey(),
                    entry.getValue().get(0).getCategory().getName(),
                    entry.getValue().stream()
                        .map(PrayResponseDto::of)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
        }
        return prayMap.entrySet().stream()
            .map(entry -> new PrayListResponseDto(entry.getKey(),
                entry.getValue().get(0).getCategory().getName(),
                entry.getValue().stream()
                    .map(Pray -> {
                        Pray originPray = prayRepository.getPrayById(Pray.getOriginPrayId());
                        return PrayResponseDto.shared(Pray, originPray);
                    })
                    .collect(Collectors.toList())))
            .collect(Collectors.toList());
    }


    @Transactional
    public List<PrayListResponseDto> completePray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        prayRepository.delete(pray);

        return getPrayList(username, pray.getPrayType().stringValue());
    }

    @Transactional
    public List<PrayListResponseDto> cancelPray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        prayRepository.cancelPray(pray);

        return getPrayList(username, PrayType.SHARED.stringValue());
    }
}
