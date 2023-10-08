package com.uspray.uspray.infrastructure.querydsl;

import static com.uspray.uspray.domain.QSharedPray.sharedPray;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.SharedPray;
import com.uspray.uspray.infrastructure.SharedPrayRepositoryCustom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SharedPrayRepositoryImpl implements SharedPrayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SharedPray> findAllByMemberId(String userId) {
        return queryFactory
            .selectFrom(sharedPray)
            .where(sharedPray.member.userId.eq(userId))
            .fetch();
    }

}
