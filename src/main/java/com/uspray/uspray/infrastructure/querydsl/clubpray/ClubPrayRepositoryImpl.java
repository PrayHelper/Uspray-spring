package com.uspray.uspray.infrastructure.querydsl.clubpray;

import static com.uspray.uspray.domain.QClubPray.*;
import static com.uspray.uspray.domain.QPray.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.clubpray.ClubPrayResponseDto;
import com.uspray.uspray.DTO.clubpray.QClubPrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Club;
import com.uspray.uspray.domain.Member;
import java.util.List;
import javax.persistence.EntityManager;

public class ClubPrayRepositoryImpl implements ClubPrayRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ClubPrayRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //Club Id, memberId, Pray중 Shared만
    @Override
    public List<ClubPrayResponseDto> getClubPrayList(Club club, Member member) {
        return queryFactory
            .select(new QClubPrayResponseDto(
                clubPray.id,
                clubPray.content,
                clubPray.author.id,
                pray.count))
            .from(pray)
            .rightJoin(pray.clubPray, clubPray)
            .where(pray.member.eq(member),
                pray.prayType.eq(PrayType.SHARED),
                clubPray.club.eq(club))
            .fetch();
    }
}
