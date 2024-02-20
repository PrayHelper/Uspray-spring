package com.uspray.uspray.infrastructure.querydsl.category;

import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import java.util.List;

public interface CategoryRepositoryCustom {

    List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType);

    List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType, List<Long> prayIds);
}
