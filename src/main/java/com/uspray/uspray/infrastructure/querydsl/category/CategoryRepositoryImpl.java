package com.uspray.uspray.infrastructure.querydsl.category;

import static com.uspray.uspray.domain.QCategory.category;
import static com.uspray.uspray.domain.QPray.pray;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.Category;
import java.util.ArrayList;
import java.util.List;
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
            List<PrayResponseDto> prays = queryFactory
                .select(Projections.constructor(
                    PrayResponseDto.class,
                    pray.id,
                    pray.content,
                    pray.member.name,
                    pray.deadline,
                    pray.category.id,
                    pray.category.name,
                    pray.lastPrayedAt))
                .from(pray)
                .where(pray.category.id.eq(cat.getId())
                    .and(pray.member.userId.eq(username))
                    .and(pray.prayType.stringValue().likeIgnoreCase(prayType)))
                .fetch();

            prayListResponseDtos.add(
                new PrayListResponseDto(cat.getId(), cat.getName(), cat.getColor(), prays));
        }
        return prayListResponseDtos;
    }
}
