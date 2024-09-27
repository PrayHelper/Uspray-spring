package com.uspray.uspray.domain.history.repository.querydsl;

import com.uspray.uspray.domain.history.dto.request.HistorySearchRequestDto;
import com.uspray.uspray.domain.history.model.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryRepositoryCustom {

    Page<History> findBySearchOption(String username, HistorySearchRequestDto historySearchRequestDto, Pageable pageable);

}
