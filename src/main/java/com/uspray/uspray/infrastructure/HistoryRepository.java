package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.querydsl.history.HistoryRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {

    Page<History> findByMemberAndOriginPrayIdIsNull(Member member, Pageable pageable);

    Page<History> findByMemberAndOriginPrayIdIsNotNull(Member member, Pageable pageable);


    History findByIdAndMember(Long historyId, Member member);

    default History getHistoryById(Long historyId) {
        return findById(historyId)
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.HISTORY_NOT_FOUND_EXCEPTION,
                ErrorStatus.HISTORY_NOT_FOUND_EXCEPTION.getMessage()
            ));
    }
}
