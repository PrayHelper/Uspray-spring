package com.uspray.uspray.domain.category.repository.querydsl;

import static com.uspray.uspray.domain.category.model.QCategory.category;
import static com.uspray.uspray.domain.pray.model.QPray.pray;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.response.QPrayResponseDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

	private final JPAQueryFactory queryFactory;


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
					.and(pray.categoryType.stringValue().likeIgnoreCase(prayType)))
				.fetch();
			prayResponseDtos.removeIf(p -> !prayIds.contains(p.getPrayId()));
			prayResponseDtos.forEach(p -> p.setInGroup(true));

			prayListResponseDtos.add(
				new PrayListResponseDto(cat.getId(), cat.getName(), cat.getColor(),
					prayResponseDtos));
		}
		return prayListResponseDtos;
	}
}
