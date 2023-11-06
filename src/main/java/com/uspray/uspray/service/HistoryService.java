package com.uspray.uspray.service;

import com.uspray.uspray.DTO.history.request.HistorySearchRequestDto;
import com.uspray.uspray.DTO.history.response.HistoryResponseDto;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.HistoryRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<HistoryResponseDto> getHistoryList(String username) {
        Member member = memberRepository.getMemberByUserId(username);
        List<History> historyList = historyRepository.findByMemberOrderByDeadlineDesc(member);
//        List<History> historyList = historyRepository.getAllByUserIdOrderByDeadlineDesc(userId);
        return historyList.stream()
            .map(HistoryResponseDto::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HistoryResponseDto> searchHistoryList(String username, HistorySearchRequestDto historySearchRequestDto) {
        Member member = memberRepository.getMemberByUserId(username);
        Set<History> set;

        if (historySearchRequestDto.getKeyword() == null || historySearchRequestDto.getKeyword().isEmpty()) {
            // 키워드 없이 날짜만 입력되었을 경우
            List<History> createdAtList = historyRepository.findByMemberAndCreatedAtBetween(member, historySearchRequestDto.getStartDate(), historySearchRequestDto.getEndDate());
            List<History> deadlineList = historyRepository.findByMemberAndDeadlineBetween(member, historySearchRequestDto.getStartDate(), historySearchRequestDto.getEndDate());

            set = new LinkedHashSet<>(createdAtList);
            set.addAll(deadlineList);
        } else {
            // 키워드 있을 경우
            List<History> createdAtList = historyRepository.findByMemberAndContentContainingAndCreatedAtBetween(member, historySearchRequestDto.getKeyword(), historySearchRequestDto.getStartDate(), historySearchRequestDto.getEndDate());
            List<History> deadlineList = historyRepository.findByMemberAndContentContainingAndDeadlineBetween(member, historySearchRequestDto.getKeyword(), historySearchRequestDto.getStartDate(), historySearchRequestDto.getEndDate());

            set = new LinkedHashSet<>(createdAtList);
            set.addAll(deadlineList);
        }
        List<History> historyList = new ArrayList<>(set);
        return historyList.stream()
            .map(HistoryResponseDto::of)
            .collect(Collectors.toList());
    }

}
