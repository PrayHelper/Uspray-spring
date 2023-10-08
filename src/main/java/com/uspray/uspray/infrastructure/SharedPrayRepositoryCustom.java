package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.SharedPray;
import java.util.List;

public interface SharedPrayRepositoryCustom {

    List<SharedPray> findAllByMemberId(String userId);
}
