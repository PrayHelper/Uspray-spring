package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayService {
    
    private final PrayRepository prayRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional
    public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username) {
        Member member = memberRepository.getMemberByUserId(username);
        Category category = categoryRepository.getCategoryById(prayRequestDto.getCategoryId());
        if (!Objects.equals(category.getMember().getId(), member.getId())) {
            throw new NotFoundException(ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION,
                ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION.getMessage());
        }
        Pray pray = prayRequestDto.toEntity(member, category, PrayType.PERSONAL);
        prayRepository.save(pray);
        return PrayResponseDto.of(pray);
    }
    
    @Transactional
    public PrayResponseDto getPrayDetail(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        return PrayResponseDto.of(pray);
    }
    
    @Transactional
    public PrayResponseDto deletePray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        prayRepository.delete(pray);
        return PrayResponseDto.of(pray);
    }
    
    @Transactional
    public PrayResponseDto updatePray(Long prayId, String username, PrayRequestDto prayRequestDto) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        Category category = categoryRepository.getCategoryById(prayRequestDto.getCategoryId());
        if (!Objects.equals(category.getMember().getId(), pray.getMember().getId())) {
            throw new NotFoundException(ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION,
                ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION.getMessage());
        }
        pray.update(prayRequestDto);
        return PrayResponseDto.of(pray);
    }
    
    @Transactional
    public List<PrayListResponseDto> getPrayList(String username, String prayType) {
        
        List<Pray> prays = prayRepository.findAllWithOrderAndType(username, prayType);
        
        // Pray 엔티티를 categoryId를 기준으로 그룹화한 맵 생성
        Map<Long, List<Pray>> prayMap = prays.stream()
            .collect(Collectors.groupingBy(pray -> pray.getCategory().getId()));
        
        // 그룹화된 맵을 PrayListResponseDto 변환하여 반환
        return prayMap.entrySet().stream()
            .map(entry -> new PrayListResponseDto(entry.getKey(),
                entry.getValue().get(0).getCategory().getName(),
                entry.getValue().stream()
                    .map(PrayResponseDto::of)
                    .collect(Collectors.toList())))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public List<PrayListResponseDto> todayPray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        LocalDate today = LocalDate.now();
        if (pray.getLastPrayedAt() == null || !pray.getLastPrayedAt().equals(today)) {
            pray.countUp();
        } else {
            throw new NotFoundException(ErrorStatus.ALREADY_PRAYED_TODAY,
                ErrorStatus.ALREADY_PRAYED_TODAY.getMessage());
        }
        return getPrayList(username, PrayType.PERSONAL.stringValue());
    }
}
