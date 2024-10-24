package com.uspray.uspray.domain.history.service;

import com.uspray.uspray.domain.history.dto.request.HistorySearchRequestDto;
import com.uspray.uspray.domain.history.dto.response.HistoryDetailResponseDto;
import com.uspray.uspray.domain.history.dto.response.HistoryListResponseDto;
import com.uspray.uspray.domain.history.dto.response.HistoryResponseDto;
import com.uspray.uspray.global.enums.PrayType;
import com.uspray.uspray.domain.history.model.History;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.domain.history.repository.HistoryRepository;
import com.uspray.uspray.domain.member.repository.MemberRepository;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final PrayRepository prayRepository;

    @Transactional(readOnly = true)
    public HistoryListResponseDto getHistoryList(String username, String type, int page, int size) {
        // type은 대소문자 구분하지 않습니다

        Pageable pageable = PageRequest.of(page, size, Sort.by("deadline").descending());
        Member member = memberRepository.getMemberByUserId(username);
        Page<HistoryResponseDto> historyList;

        if (PrayType.PERSONAL.name().equalsIgnoreCase(type)) {
            historyList = historyRepository.findByMemberAndOriginPrayIdIsNull(member, pageable)
                    .map(HistoryResponseDto::of);
        }
        else {
            historyList = historyRepository.findByMemberAndOriginPrayIdIsNotNull(
                member, pageable).map(history -> {
                Member originMember = memberRepository.getMemberById(history.getOriginMemberId());
                return HistoryResponseDto.shared(history, originMember);
            });
        }
        return new HistoryListResponseDto(historyList.getContent(), historyList.getTotalPages());
    }

    @Transactional(readOnly = true)
    public HistoryListResponseDto searchHistoryList(String username,
        HistorySearchRequestDto historySearchRequestDto) {

        Pageable pageable = PageRequest.of(historySearchRequestDto.getPage(),
            historySearchRequestDto.getSize(), Sort.by("deadline").descending());
        Page<HistoryResponseDto> historyList = historyRepository.findBySearchOption(username,
                historySearchRequestDto, pageable)
            .map(HistoryResponseDto::of);
        return new HistoryListResponseDto(historyList.getContent(), historyList.getTotalPages());
    }

    @Transactional(readOnly = true)
    public HistoryDetailResponseDto getHistoryDetail(String username, Long historyId) {
        Member member = memberRepository.getMemberByUserId(username);
        History history = historyRepository.getHistoryById(historyId);
        if (history.getPrayType().equals(PrayType.SHARED)) {
            Pray originPray = prayRepository.getPrayById(history.getOriginPrayId());
            return HistoryDetailResponseDto.shared(history, originPray);
        }
        return HistoryDetailResponseDto.of(history);
    }

    @Transactional
    public void saveHistory(History history) {
        historyRepository.save(history);
    }

    @Transactional
    public void deleteHistory(History history) {
        historyRepository.delete(history);
    }

    @Transactional
    public History getHistoryByIdAndMember(Long historyId, Member member) {
        return historyRepository.getHistoryByIdAndMember(historyId, member);
    }
}
