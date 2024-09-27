package com.uspray.uspray.domain.category.repository.querydsl;

import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import java.util.List;

public interface CategoryRepositoryCustom {

    List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType);

    List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType, List<Long> prayIds);
}
