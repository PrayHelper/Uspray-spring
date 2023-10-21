package com.uspray.uspray.service;

import com.uspray.uspray.DTO.history.response.HistoryResponseDto;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.infrastructure.HistoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    @Transactional(readOnly = true)
    public List<HistoryResponseDto> getHistoryList(String userId) {
        List<History> historyList = historyRepository.getAllByUserIdOrderByDeadlineDesc(userId);
        return historyList.stream()
            .map(HistoryResponseDto::of)
            .collect(Collectors.toList());
    }

}
