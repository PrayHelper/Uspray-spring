package com.uspray.uspray.service;

import com.uspray.uspray.DTO.history.response.HistoryDetailResponseDto;
import com.uspray.uspray.DTO.history.response.HistoryListResponseDto;
import com.uspray.uspray.DTO.history.response.HistoryResponseDto;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.infrastructure.HistoryRepository;

import com.uspray.uspray.infrastructure.PrayRepository;
import java.time.LocalDate;
import java.util.List;

import com.uspray.uspray.infrastructure.MemberRepository;
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
    public HistoryListResponseDto getHistoryList(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("deadline").descending());
        Member member = memberRepository.getMemberByUserId(username);
        Page<HistoryResponseDto> historyList = historyRepository.findByMember(member, pageable).map(HistoryResponseDto::of);
        return new HistoryListResponseDto(historyList.getContent(), historyList.getTotalPages());
    }

    @Transactional(readOnly = true)
    public HistoryListResponseDto searchHistoryList(String username, String keyword, Boolean isMine, Boolean isShared, LocalDate startDate, LocalDate endDate, int page, int size) {

        // 전체 파라미터가 null 인 경우 예외처리
        if (keyword == null && isMine == null && isShared == null && startDate == null && endDate == null) {
            throw new IllegalArgumentException("검색 조건이 없습니다.");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("deadline").descending());
        Page<HistoryResponseDto> historyList = historyRepository.findBySearchOption(username, keyword, isMine, isShared, startDate, endDate, pageable).map(HistoryResponseDto::of);
        return new HistoryListResponseDto(historyList.getContent(), historyList.getTotalPages());
    }

    @Transactional(readOnly = true)
    public HistoryDetailResponseDto getHistoryDetail(String username, Long historyId) {
        Member member = memberRepository.getMemberByUserId(username);
        History history = historyRepository.findById(historyId).orElseThrow(() -> new IllegalArgumentException("해당 히스토리가 없습니다. id=" + historyId));
        if (!history.getMember().equals(member)) {
            throw new IllegalArgumentException("해당 히스토리에 대한 권한이 없습니다.");
        }
        return HistoryDetailResponseDto.of(history);
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

}
