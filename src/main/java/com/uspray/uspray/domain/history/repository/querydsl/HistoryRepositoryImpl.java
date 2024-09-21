package com.uspray.uspray.domain.history.repository.querydsl;

import static com.uspray.uspray.domain.QHistory.history;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.history.dto.request.HistorySearchRequestDto;
import com.uspray.uspray.domain.history.model.History;
import java.time.LocalDate;
import java.util.Base64;
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
    public Page<History> findBySearchOption(String username,
        HistorySearchRequestDto historySearchRequestDto, Pageable pageable) {

        List<History> results = queryFactory
            .selectFrom(history)
            .where(
                history.member.userId.eq(username),
                keywordEq(historySearchRequestDto.getKeyword()),
                personalEq(historySearchRequestDto.getIsPersonal(),
                    historySearchRequestDto.getIsShared()),
                dateEq(historySearchRequestDto.getStartDate(), historySearchRequestDto.getEndDate())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    private BooleanExpression keywordEq(String keyword) {
        return keyword != null && !keyword.isEmpty() ? history.content.containsIgnoreCase(
            new String(
                Base64.getEncoder().encode(keyword.getBytes())))
            : null;
    }

    private BooleanExpression personalEq(Boolean isPersonal, Boolean isShared) {
        if (isPersonal == isShared) {
            return null;
        }
        return isPersonal && !isShared ? history.originPrayId.isNull()
            : history.originPrayId.isNotNull();
    }

    private BooleanExpression dateEq(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null ? history.startDate.before(
            endDate).and(history.deadline.after(startDate)) : null;
    }
}
