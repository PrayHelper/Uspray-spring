package com.uspray.uspray.domain.group.repository.querydsl;

import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayResponseDto;
import java.util.List;

public interface GroupPrayRepositoryCustom{

    List<GroupPrayResponseDto> findGroupPray(Long groupId, Long memberId);
}
