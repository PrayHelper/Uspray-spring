package com.uspray.uspray.domain.group.repository.querydsl;

import static com.uspray.uspray.domain.group.model.QGroupPray.groupPray;
import static com.uspray.uspray.domain.group.model.QScrapAndHeart.scrapAndHeart;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.domain.group.dto.grouppray.QGroupPrayResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupPrayRepositoryImpl implements GroupPrayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GroupPrayResponseDto> findGroupPray(Long groupId, Long memberId) {
        return queryFactory
            .select(new QGroupPrayResponseDto(
                groupPray,
                Expressions.constant(memberId),
                scrapAndHeart
            ))
            .from(groupPray)
            .where(groupPray.group.id.eq(groupId))
            .leftJoin(groupPray.scrapAndHeart, scrapAndHeart)
            .on(scrapAndHeart.member.id.eq(memberId))
            .fetch();
    }
}
