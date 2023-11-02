package com.uspray.uspray.infrastructure.querydsl.pray;

import static com.uspray.uspray.domain.QPray.pray;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.Pray;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrayRepositoryImpl implements PrayRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Pray> findAllWithOrder(String orderType, String username) {
    return queryFactory.
        selectFrom(pray)
        .where(pray.member.userId.eq(username), pray.deleted.eq(false))
        .orderBy(orderType.equals("date") ? pray.createdAt.desc() : pray.count.asc())
        .fetch();
  }
}

