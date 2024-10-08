package com.uspray.uspray.domain.group.dto.grouppray;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPrayRappingDto {

    Long heartCount;
    Boolean notificationAgree;

    Map<LocalDate, List<GroupPrayResponseDto>> groupPray;

}
