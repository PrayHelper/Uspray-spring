package com.uspray.uspray.infrastructure.querydsl.grouppray;

import static com.uspray.uspray.domain.QGroupPray.groupPray;
import static com.uspray.uspray.domain.QScrapAndHeart.scrapAndHeart;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.DTO.grouppray.QGroupPrayResponseDto;
import java.util.List;
import javax.persistence.EntityManager;

public class GroupPrayRepositoryImpl implements GroupPrayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GroupPrayRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //Group Id, memberId, Pray중 Shared만
    @Override
    public List<GroupPrayResponseDto> getGroupPrayList(Long groupId, String userId) {
        return queryFactory
            .select(new QGroupPrayResponseDto(
                groupPray.id,
                groupPray.content,
                groupPray.author.name,
                groupPray.author.id,
                scrapAndHeart.member.id.coalesce(-1L).as(scrapAndHeart.member.id),
                scrapAndHeart.heart.coalesce(false).as(scrapAndHeart.heart),
                scrapAndHeart.scrap.coalesce(false).as(scrapAndHeart.scrap),
                groupPray.createdAt))
            .from(groupPray)
            .leftJoin(groupPray.scrapAndHeart, scrapAndHeart)
            .where(groupPray.group.id.eq(groupId),
                scarpAndHeartEq(userId))
            .orderBy()
            .fetch();
    }

    private BooleanExpression scarpAndHeartEq(String userId) {
        return queryFactory
            .selectFrom(scrapAndHeart)
            .where(scrapAndHeart.member.userId.eq(userId))
            .fetch().size() == 0 ? null : scrapAndHeart.member.userId.eq(userId);
    }
}
