package com.uspray.uspray.infrastructure.querydsl.group;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.QGroup;
import com.uspray.uspray.domain.QMember;
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

        return queryFactory
                .select(Projections.constructor(GroupResponseDto.class, group, group.leader.eq(member)))
                .from(group)
                .leftJoin(group.members, memberEntity)
                .where(group.members.contains(member))
                .fetch();
    }
}
