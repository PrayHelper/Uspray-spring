package com.uspray.uspray.infrastructure.querydsl.history;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.QHistory;
import com.uspray.uspray.infrastructure.HistoryRepositoryCustom;
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
        BooleanExpression predicate = history.member.userId.eq(username);

        if (keyword != null && !keyword.isEmpty()) {
            predicate = predicate.and(history.content.containsIgnoreCase(keyword));
        }

        if (Boolean.TRUE.equals(isPersonal) && !Boolean.TRUE.equals(isShared)) {
            // Only personal, originPrayID가 null인 것
            predicate = predicate.and(history.originPrayId.isNull());
        }

        if (Boolean.TRUE.equals(isShared) && !Boolean.TRUE.equals(isPersonal)) {
            // Only shared, originPrayId가 null이 아닌 것
            predicate = predicate.and(history.originPrayId.isNotNull());
        }

        // 만약 둘 다 true(두 체크박스 모두 선택) 또는 false(두 체크박스 선택 안함) 라면, 아무 조건도 추가하지 않습니다.
        // 즉, isPersonal과 isShared 둘 다 true, false인 경우 모든 결과를 포함합니다.

        if (startDate != null) {
            predicate = predicate.and(history.createdAt.before(endDate.atStartOfDay()));
        }

        if (endDate != null) {
            predicate = predicate.and(history.deadline.after(startDate));
        }

        // fetchCount가 deprecated 됐다고 해서 추후 수정 예정
        long total = queryFactory
            .select(history)
            .from(history)
            .where(predicate)
            .fetchCount();

        List<History> results = queryFactory
            .selectFrom(history)
            .where(predicate)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, total);
    }
}
