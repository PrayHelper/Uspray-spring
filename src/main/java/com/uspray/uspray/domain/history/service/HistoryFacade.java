package com.uspray.uspray.domain.history.service;

import com.uspray.uspray.domain.member.service.MemberService;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.history.model.History;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.service.PrayFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryFacade {

    private final PrayFacade prayFacade;
    private final MemberService memberService;
    private final HistoryService historyService;

    @Transactional
    public PrayResponseDto historyToPray(PrayRequestDto prayRequestDto, String username, Long historyId) {
        Member member = memberService.findMemberByUserId(username);
        History history = historyService.getHistoryByIdAndMember(historyId, member);

        historyService.deleteHistory(history);

        return prayFacade.createPray(prayRequestDto, username,
            history.getStartDate());
    }


}
