package com.uspray.uspray.infrastructure.querydsl.grouppray;

import static com.uspray.uspray.domain.QGroupPray.groupPray;
import static com.uspray.uspray.domain.QPray.pray;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.DTO.grouppray.QGroupPrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.Member;
import java.util.List;
import javax.persistence.EntityManager;

public class GroupPrayRepositoryImpl implements GroupPrayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GroupPrayRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //Group Id, memberId, Pray중 Shared만
    @Override
    public List<GroupPrayResponseDto> getGroupPrayList(Group group, Member member) {
        return queryFactory
            .select(new QGroupPrayResponseDto(
                groupPray.id,
                groupPray.content,
                groupPray.author.id,
                pray.count))
            .from(pray)
            .rightJoin(pray.groupPray, groupPray)
            .where(pray.member.eq(member),
                pray.prayType.eq(PrayType.SHARED),
                groupPray.group.eq(group))
            .fetch();
    }
}
