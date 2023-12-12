package com.uspray.uspray.infrastructure.querydsl.group;

import static com.uspray.uspray.domain.QGroup.group;
import static com.uspray.uspray.domain.QMember.member;
import static com.uspray.uspray.domain.QGroupPray.groupPray;
import static com.uspray.uspray.domain.QGroupMember.groupMember;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.auth.response.MemberResponseDto;
import com.uspray.uspray.DTO.auth.response.QMemberResponseDto;
import com.uspray.uspray.DTO.group.response.GroupMemberResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.DTO.group.response.QGroupMemberResponseDto;
import com.uspray.uspray.DTO.group.response.QGroupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
                groupPray.content.max(),
                group.groupMemberList.size(),
                group.groupPrayList.size(),
                groupPray.createdAt.max(),
                group.leader.userId.eq(userId)
            ))
            .from(group)
            .join(group.groupMemberList, groupMember)
            .leftJoin(group.groupPrayList, groupPray)
            .where(groupMember.member.userId.eq(userId))
            .groupBy(group.id)
            .fetch();
    }

    @Override
    public List<GroupMemberResponseDto> findGroupMembersByGroupAndNameLike(Long groupId, String name) {

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
