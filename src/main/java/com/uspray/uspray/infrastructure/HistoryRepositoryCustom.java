package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.History;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryRepositoryCustom {

    Page<History> findBySearchOption(String username, String keyword, Boolean isMine, Boolean isShared, LocalDate startDate, LocalDate endDate, Pageable pageable);

}
