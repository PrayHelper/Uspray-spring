package com.uspray.uspray.infrastructure.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.QPray;
import com.uspray.uspray.infrastructure.PrayRepositoryCustom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrayRepositoryImpl implements PrayRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public List<Pray> findAllWithOrder(String orderType, String username) {
    return queryFactory.
        selectFrom(QPray.pray)
        .where(QPray.pray.member.userId.eq(username), QPray.pray.deleted.eq(false))
        .orderBy(orderType.equals("date") ? QPray.pray.createdAt.desc() : QPray.pray.count.asc())
        .fetch();
  }
}

