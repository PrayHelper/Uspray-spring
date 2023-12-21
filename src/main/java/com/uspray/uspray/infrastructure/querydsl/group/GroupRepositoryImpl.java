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
                groupPray.content,        // 최근 기도 내용
                groupMember.count(),      // 그룹 멤버 수 (집계 함수 사용)
                groupPray.count(),        // 그룹 기도 수 (집계 함수 사용)
                groupPray.createdAt.max(),// 기도 생성 날짜의 최대값
                group.leader.userId.eq(userId)  // 리더 ID가 사용자 ID와 일치하는지
            ))
            .from(group)
            .leftJoin(group.groupMemberList, groupMember)
            .leftJoin(group.groupPrayList, groupPray)
            .groupBy(group.id, group.name, group.leader.userId, groupPray.content)
            .orderBy(groupPray.createdAt.max().desc())
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
