package com.uspray.uspray.infrastructure.querydsl.grouppray;

import com.uspray.uspray.DTO.grouppray.GroupPrayResponseDto;
import java.util.List;

public interface GroupPrayRepositoryCustom {

    List<GroupPrayResponseDto> getGroupPrayList(Long groupId, String userId);

}
