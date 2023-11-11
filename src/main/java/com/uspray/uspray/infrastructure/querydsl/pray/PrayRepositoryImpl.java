package com.uspray.uspray.infrastructure.querydsl.pray;

import static com.uspray.uspray.domain.QCategory.category;
import static com.uspray.uspray.domain.QPray.pray;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.Pray;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrayRepositoryImpl implements PrayRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public List<Pray> findAllWithOrderAndType(String username, String prayType) {
        return queryFactory
            .select(pray)
            .from(pray)
            .join(pray.category, category)
            .where(category.member.userId.eq(username))
            .where(pray.prayType.stringValue().likeIgnoreCase(prayType))
            .orderBy(pray.createdAt.asc())
            .fetch();
    }
}

