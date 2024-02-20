package com.uspray.uspray.infrastructure.querydsl.category;

import static com.uspray.uspray.domain.QCategory.category;
import static com.uspray.uspray.domain.QPray.pray;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.DTO.pray.response.QPrayResponseDto;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Pray;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType) {
        // 카테고리 목록 가져오기
        List<Category> categories = queryFactory
            .selectFrom(category)
            .where(category.member.userId.eq(username))
            .where(category.categoryType.stringValue().likeIgnoreCase(prayType))
            .orderBy(category.order.asc())
            .fetch();

        // 각 카테고리 별로 PrayResponseDto 목록 가져오기
        List<PrayListResponseDto> prayListResponseDtos = new ArrayList<>();
        for (Category cat : categories) {
            List<Pray> prays = queryFactory
                .selectFrom(pray)
                .where(pray.category.id.eq(cat.getId())
                    .and(pray.member.userId.eq(username))
                    .and(pray.prayType.stringValue().likeIgnoreCase(prayType)))
                .fetch();

            List<PrayResponseDto> prayResponseDtos = prays.stream()
                .map(pray_iter -> {
                    Pray originPray = findOriginPray(pray_iter);
                    if (originPray != null) {
                        return PrayResponseDto.shared(pray_iter, originPray);
                    } else {
                        return PrayResponseDto.of(pray_iter);
                    }
                })
                .collect(Collectors.toList());

            prayListResponseDtos.add(
                new PrayListResponseDto(cat.getId(), cat.getName(), cat.getColor(),
                    prayResponseDtos));
        }
        return prayListResponseDtos;
    }

    private Pray findOriginPray(Pray target_pray) {
        return queryFactory.selectFrom(pray)
            .where(pray.id.eq(target_pray.getOriginPrayId()))
            .fetchOne();
    }

    @Override
    public List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType,
        List<Long> prayIds) {
        List<Category> categories = queryFactory
            .selectFrom(category)
            .where(category.member.userId.eq(username))
            .where(category.categoryType.stringValue().likeIgnoreCase(prayType))
            .orderBy(category.order.asc())
            .fetch();

        // 각 카테고리 별로 PrayResponseDto 목록 가져오기
        List<PrayListResponseDto> prayListResponseDtos = new ArrayList<>();
        for (Category cat : categories) {
            List<PrayResponseDto> prayResponseDtos = queryFactory
                .select(new QPrayResponseDto(
                    pray.id,
                    pray.content,
                    pray.member.name,
                    pray.deadline,
                    pray.category.id,
                    pray.category.name,
                    pray.lastPrayedAt,
                    pray.isShared
                ))
                .from(pray)
                .where(pray.category.id.eq(cat.getId())
                    .and(pray.member.userId.eq(username))
                    .and(pray.prayType.stringValue().likeIgnoreCase(prayType)))
                .fetch();

            prayResponseDtos.stream().filter(p -> prayIds.contains(p.getPrayId()))
                .forEach(p -> p.setInGroup(true));

            prayListResponseDtos.add(
                new PrayListResponseDto(cat.getId(), cat.getName(), cat.getColor(),
                    prayResponseDtos));
        }
        return prayListResponseDtos;
    }
}
