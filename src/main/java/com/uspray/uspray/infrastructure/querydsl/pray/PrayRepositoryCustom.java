package com.uspray.uspray.infrastructure.querydsl.pray;

import com.uspray.uspray.domain.Pray;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface PrayRepositoryCustom {

    List<Pray> findAllWithOrderAndType(String username, String prayType);

    @Nullable
    Integer getSharedCountByOriginPrayId(Long prayId);
}
