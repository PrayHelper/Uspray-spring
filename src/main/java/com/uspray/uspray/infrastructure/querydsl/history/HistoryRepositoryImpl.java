package com.uspray.uspray.infrastructure.querydsl.history;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.QHistory;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HistoryRepositoryImpl implements HistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<History> findBySearchOption(String username, String keyword, Boolean isPersonal,
        Boolean isShared, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        QHistory history = QHistory.history;

        List<History> results = queryFactory
            .selectFrom(history)
            .where(
                history.member.userId.eq(username),
                keyword != null && !keyword.isEmpty() ? history.content.containsIgnoreCase(keyword) : null,
                Boolean.TRUE.equals(isPersonal) && !Boolean.TRUE.equals(isShared) ? history.originPrayId.isNull() : null,
                Boolean.TRUE.equals(isShared) && !Boolean.TRUE.equals(isPersonal) ? history.originPrayId.isNotNull() : null,
                startDate != null ? history.createdAt.before(endDate.atStartOfDay()) : null,
                endDate != null ? history.deadline.after(startDate) : null
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }
}
