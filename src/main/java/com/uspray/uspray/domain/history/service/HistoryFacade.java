package com.uspray.uspray.domain.history.service;

import com.uspray.uspray.domain.pray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.history.model.History;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.service.PrayFacade;
import com.uspray.uspray.domain.history.repository.HistoryRepository;
import com.uspray.uspray.domain.member.repository.MemberRepository;
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
