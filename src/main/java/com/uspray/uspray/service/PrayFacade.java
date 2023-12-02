package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
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
public class PrayFacade {

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
    public PrayResponseDto updatePray(Long prayId, String username,
        PrayUpdateRequestDto prayUpdateRequestDto) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        categoryRepository.getCategoryByIdAndMember(
            prayUpdateRequestDto.getCategoryId(),
            pray.getMember());

        // 그룹 기도 제목은 이 API로 수정 불가능
        if (pray.getPrayType() == PrayType.GROUP) {
            throw new CustomException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
                ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
        }
        // 이 기도 제목을 공유한 적 없거나, 공유 받은 사람이 없으면 전부 수정 가능
        // 이 기도 제목을 공유한 적 있고, 누구라도 공유 받은 사람이 있으면 기도제목 내용 수정 불가능
        Pray sharedPray = prayRepository.getPrayByOriginPrayId(prayId);
        if (prayUpdateRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.getCategoryByIdAndMember(
                prayUpdateRequestDto.getCategoryId(),
                pray.getMember());
            pray.update(prayUpdateRequestDto,
                sharedPray != null || pray.getPrayType() == PrayType.SHARED, category);
        }

        pray.update(prayUpdateRequestDto,
            sharedPray != null || pray.getPrayType() == PrayType.SHARED, null);

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
