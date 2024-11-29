package com.uspray.uspray.domain.pray.repository.querydsl;

import com.uspray.uspray.domain.pray.model.Pray;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface PrayRepositoryCustom {

    List<Pray> findAllWithOrderAndType(String username, String prayType);

    @Nullable
    Integer getSharedCountByIdAndOriginPrayId(Long prayId, Long originPrayId);
}
