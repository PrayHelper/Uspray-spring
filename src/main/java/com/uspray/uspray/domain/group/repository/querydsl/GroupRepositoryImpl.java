package com.uspray.uspray.domain.group.repository.querydsl;

import static com.uspray.uspray.domain.group.model.QGroup.group;
import static com.uspray.uspray.domain.group.model.QGroupMember.groupMember;
import static com.uspray.uspray.domain.group.model.QGroupPray.groupPray;
import static com.uspray.uspray.domain.member.model.QMember.member;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.group.dto.group.response.GroupMemberResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.QGroupMemberResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.QGroupResponseDto;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GroupResponseDto> findGroupListByMemberId(String userId) {
        List<GroupResponseDto> result = queryFactory
            .select(new QGroupResponseDto(
                group.id,
                group.name,
                groupPray.content,        // 최근 기도 내용
                group.groupMemberList.size(),      // 그룹 멤버 수 (집계 함수 사용)
                group.groupPrayList.size(),        // 그룹 기도 수 (집계 함수 사용)
                groupPray.createdAt.max(),// 기도 생성 날짜의 최대값
                group.leader.userId.eq(userId)  // 리더 ID가 사용자 ID와 일치하는지
            ))
            .from(group)
            .leftJoin(group.groupMemberList, groupMember)
            .leftJoin(group.groupPrayList, groupPray)
            .where(groupMember.member.userId.eq(userId))
            .groupBy(group.id, group.name, group.leader.userId, groupPray.content)
            .orderBy(groupPray.createdAt.max().desc())
            .fetch();
        return result.stream()
            .collect(Collectors.groupingBy(GroupResponseDto::getId))
            .values().stream()
            .map(groupResponseDtos -> groupResponseDtos.stream()
                .max(Comparator.comparing(GroupResponseDto::getUpdatedAt))
                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다.")))
            .sorted(
                Comparator.comparing(GroupResponseDto::getUpdatedAt,
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                    .reversed()) // 최신 업데이트 날짜로 정렬
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupMemberResponseDto> findGroupMembersByGroupAndNameLikeExceptUser(Long groupId,
        String name, String username) {

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
            .where(member.userId.ne(username))
            .fetch();
    }

}
