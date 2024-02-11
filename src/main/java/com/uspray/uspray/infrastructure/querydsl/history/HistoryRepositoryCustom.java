package com.uspray.uspray.infrastructure.querydsl.history;

import com.uspray.uspray.DTO.history.request.HistorySearchRequestDto;
import com.uspray.uspray.domain.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryRepositoryCustom {

    Page<History> findBySearchOption(String username, HistorySearchRequestDto historySearchRequestDto, Pageable pageable);

}
