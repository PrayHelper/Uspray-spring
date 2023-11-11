package com.uspray.uspray.infrastructure.querydsl.pray;

import com.uspray.uspray.domain.Pray;
import java.util.List;

public interface PrayRepositoryCustom {
    
    List<Pray> findAllWithOrderAndType(String username, String prayType);
    
}
