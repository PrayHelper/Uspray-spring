package com.uspray.uspray.infrastructure.querydsl.group;

import static com.uspray.uspray.domain.QGroup.group;
import static com.uspray.uspray.domain.QMember.member;
import static com.uspray.uspray.domain.QGroupPray.groupPray;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GroupResponseDto> findGroupListByMember(Member target) {

        return queryFactory
            .select(Projections.constructor(
                GroupResponseDto.class,
                group.id,
                group.name,
                groupPray.content.max(),
                group.members.size(),
                group.groupPrayList.size(),
                groupPray.createdAt.max()
            ))
            .from(group)
            .leftJoin(group.groupPrayList, groupPray)
            .leftJoin(group.members, member)
            .where(group.members.contains(target))
            .groupBy(group.id)
            .fetch();
    }
}
