package com.uspray.uspray.infrastructure.querydsl.grouppray;

//TODO 리팩터링하거나 지우거나
public class GroupPrayRepositoryImpl {

//    private final JPAQueryFactory queryFactory;
//
//    public GroupPrayRepositoryImpl(EntityManager em) {
//        this.queryFactory = new JPAQueryFactory(em);
//    }

    //Group Id, memberId, Pray중 Shared만
//    @Override
//    public List<GroupPrayResponseDto> getGroupPrayList(Long groupId, String userId) {
//        return queryFactory
//            .select(new QGroupPrayResponseDto(
//                groupPray.id,
//                groupPray.content,
//                groupPray.author.name,
//                groupPray.author.id,
//                scrapAndHeart.member.id.coalesce(-1L).as(scrapAndHeart.member.id),
//                scrapAndHeart.heart.coalesce(false).as(scrapAndHeart.heart),
//                scrapAndHeart.scrap.coalesce(false).as(scrapAndHeart.scrap),
//                groupPray.createdAt))
//            .from(groupPray)
//            .leftJoin(groupPray.scrapAndHeart, scrapAndHeart)
//            .on(scarpAndHeartEq(userId))
//            .where(groupPray.group.id.eq(groupId),
//                groupPray.deadline.gt(LocalDate.now()))
//            .orderBy()
//            .fetch();
//    }
//
//    private BooleanExpression scarpAndHeartEq(String userId) {
//        return queryFactory
//            .selectFrom(scrapAndHeart)
//            .where(scrapAndHeart.member.userId.eq(userId))
//            .fetch().size() == 0 ? null : scrapAndHeart.member.userId.eq(userId);
//    }
}
