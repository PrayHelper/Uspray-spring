package com.uspray.uspray.infrastructure.querydsl.grouppray;

import com.uspray.uspray.DTO.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.Member;
import java.util.List;

public interface GroupPrayRepositoryCustom {

    List<GroupPrayResponseDto> getGroupPrayList(Group group, Member member);

}
