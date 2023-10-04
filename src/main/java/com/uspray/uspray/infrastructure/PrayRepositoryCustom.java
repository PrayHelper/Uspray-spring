package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Pray;
import java.util.List;

public interface PrayRepositoryCustom {

  List<Pray> findAllWithOrder(String orderType, String username);

}
