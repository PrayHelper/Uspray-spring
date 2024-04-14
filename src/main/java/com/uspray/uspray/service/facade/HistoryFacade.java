package com.uspray.uspray.service.facade;

import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.HistoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryFacade {

    private final PrayFacade prayFacade;
    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    public PrayResponseDto historyToPray(PrayRequestDto prayRequestDto, String username, Long historyId) {
        Member member = memberRepository.getMemberByUserId(username);
        History history = historyRepository.getHistoryByIdAndMember(historyId, member);

        historyRepository.delete(history);

        return prayFacade.createPray(prayRequestDto, username,
            history.getStartDate());
    }


}
