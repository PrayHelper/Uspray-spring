package com.uspray.uspray.domain.pray.repository.querydsl;

import static com.uspray.uspray.domain.category.model.QCategory.category;
import static com.uspray.uspray.domain.pray.model.QPray.pray;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.pray.model.Pray;
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
            .orderBy(pray.category.order.asc())
            .fetch();
    }

    @Override
    public Integer getSharedCountByIdAndOriginPrayId(Long prayId, Long originPrayId) {
        return queryFactory
            .select(pray.count.sum())
            .from(pray)
            .where(eqPrayIdOrOriginPrayId(prayId, originPrayId))
            .fetchOne();
    }

    private BooleanExpression eqPrayIdOrOriginPrayId(Long prayId, Long originPrayId){
        if(originPrayId == null){
            return pray.id.eq(prayId);
        }
        return pray.originPrayId.eq(originPrayId).or(pray.id.eq(originPrayId));
    }
}

