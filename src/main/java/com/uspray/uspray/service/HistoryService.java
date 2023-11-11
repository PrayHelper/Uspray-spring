package com.uspray.uspray.service;

import com.uspray.uspray.DTO.history.response.HistoryDetailResponseDto;
import com.uspray.uspray.DTO.history.response.HistoryListResponseDto;
import com.uspray.uspray.DTO.history.response.HistoryResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.HistoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import java.time.LocalDate;
import java.util.List;
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
        } else if (PrayType.SHARED.name().equalsIgnoreCase(type)) {
            historyList = historyRepository.findByMemberAndOriginPrayIdIsNotNull(
                member, pageable).map(HistoryResponseDto::of);
        } else {
            throw new IllegalArgumentException("잘못된 타입입니다.");
        }
        return new HistoryListResponseDto(historyList.getContent(),
            historyList.getTotalPages());
    }
    
    @Transactional(readOnly = true)
    public HistoryListResponseDto searchHistoryList(String username, String keyword,
        Boolean isPersonal, Boolean isShared, LocalDate startDate, LocalDate endDate, int page,
        int size) {
        
        // 전체 파라미터가 null 인 경우 예외처리
        if (keyword == null && isPersonal == null && isShared == null && startDate == null
            && endDate == null) {
            throw new IllegalArgumentException("검색 조건이 없습니다.");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("deadline").descending());
        Page<HistoryResponseDto> historyList = historyRepository.findBySearchOption(username,
                keyword, isPersonal, isShared, startDate, endDate, pageable)
            .map(HistoryResponseDto::of);
        return new HistoryListResponseDto(historyList.getContent(), historyList.getTotalPages());
    }
    
    @Transactional(readOnly = true)
    public HistoryDetailResponseDto getHistoryDetail(String username, Long historyId) {
        Member member = memberRepository.getMemberByUserId(username);
        History history = historyRepository.findById(historyId)
            .orElseThrow(() -> new IllegalArgumentException("해당 히스토리가 없습니다. id=" + historyId));
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
    
    public void createHistory(String username, Long prayId) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        pray.complete();
        History history = History.builder()
            .pray(pray)
            .build();
        historyRepository.save(history);
    }
    
    public void deleteHistory(Long historyId, String username) {
        Member member = memberRepository.getMemberByUserId(username);
        History history = historyRepository.findByIdAndMember(historyId, member);
        if (history == null) {
            throw new NotFoundException(ErrorStatus.HISTORY_NOT_FOUND_EXCEPTION,
                ErrorStatus.HISTORY_NOT_FOUND_EXCEPTION.getMessage());
        }
        historyRepository.delete(history);
    }
}
