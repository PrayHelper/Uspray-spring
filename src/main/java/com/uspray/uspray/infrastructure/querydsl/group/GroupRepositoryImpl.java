package com.uspray.uspray.infrastructure.querydsl.group;

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
    public List<GroupResponseDto> findGroupListByMember(Member member) {

        QGroup group = QGroup.group;
        QMember memberEntity = QMember.member;
        QGroupPray groupPray = QGroupPray.groupPray;

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
            .leftJoin(group.members, memberEntity)
            .where(group.members.contains(member))
            .groupBy(group.id)
            .fetch();
    }
}
