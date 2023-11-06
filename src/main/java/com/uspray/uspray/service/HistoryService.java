package com.uspray.uspray.service;

import com.uspray.uspray.DTO.history.request.HistorySearchRequestDto;
import com.uspray.uspray.DTO.history.response.HistoryResponseDto;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.HistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public List<HistoryResponseDto> searchHistoryList(String username, String keyword, Boolean isMine, Boolean isShared, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.getMemberByUserId(username);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        List<History> historyList = new ArrayList<>();

        if (keyword == null || keyword.isEmpty()) {
            // 키워드 없이 날짜만 입력되었을 경우
            historyList = historyRepository.findAllByPeriodOverlap(startDate, endDateTime, member);
        } else {
            // 키워드 있을 경우
            historyList = historyRepository.findAllByKeywordAndPeriodOverlap(keyword, startDate, endDateTime, member);
        }
        return historyList.stream()
            .map(HistoryResponseDto::of)
            .collect(Collectors.toList());
    }

}
