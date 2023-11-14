package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.HistoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayFacadeService {

    private final MemberRepository memberRepository;
    private final PrayRepository prayRepository;
    private final CategoryRepository categoryRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username) {
        Member member = memberRepository.getMemberByUserId(username);
        Category category = categoryRepository.getCategoryByIdAndMember(
            prayRequestDto.getCategoryId(),
            member);
        Pray pray = prayRequestDto.toEntity(member, category, PrayType.PERSONAL);
        prayRepository.save(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public PrayResponseDto updatePray(Long prayId, String username, PrayRequestDto prayRequestDto) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        categoryRepository.getCategoryByIdAndMember(
            prayRequestDto.getCategoryId(),
            pray.getMember());
        pray.update(prayRequestDto);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public void convertPrayToHistory() {
        List<Pray> prayList = prayRepository.findAllByDeadlineBefore(LocalDate.now());
        for (Pray pray : prayList) {
            History history = History.builder()
                .pray(pray)
                .build();
            historyRepository.save(history);
            prayRepository.delete(pray);
        }
    }


    public void createHistory(String username, Long prayId) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        pray.complete();
        History history = History.builder()
            .pray(pray)
            .build();
        historyRepository.save(history);
    }
}
