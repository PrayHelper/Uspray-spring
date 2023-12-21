package com.uspray.uspray.infrastructure.querydsl.group;

import static com.uspray.uspray.domain.QGroup.group;
import static com.uspray.uspray.domain.QGroupMember.groupMember;
import static com.uspray.uspray.domain.QGroupPray.groupPray;
import static com.uspray.uspray.domain.QMember.member;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.group.response.GroupMemberResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.DTO.group.response.QGroupMemberResponseDto;
import com.uspray.uspray.DTO.group.response.QGroupResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GroupResponseDto> findGroupListByMemberId(String userId) {

        return queryFactory
            .select(new QGroupResponseDto(
                group.id,
                group.name,
                group.lastPray.content,
                group.groupMemberList.size(),
                group.groupPrayList.size(),
                group.lastPray.createdAt,
                group.leader.userId.eq(userId)
            ))
            .from(group)
            .leftJoin(group.lastPray)
            .join(group.groupMemberList, groupMember)
            .where(groupMember.member.userId.eq(userId))
            .groupBy(group.id, group.name, group.leader.userId)
            .fetch();
    }

    @Override
    public List<GroupMemberResponseDto> findGroupMembersByGroupAndNameLike(Long groupId,
        String name) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(groupMember.group.id.eq(groupId));

        if (StringUtils.hasText(name)) {
            builder.and(member.name.likeIgnoreCase("%" + name + "%"));
        }

        return queryFactory
            .select(new QGroupMemberResponseDto(
                member.id,
                member.userId,
                member.name
            ))
            .from(member)
            .join(member.groupMemberList, groupMember)
            .where(builder)
            .fetch();
    }

}
