package com.uspray.uspray.DTO.grouppray;

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

    Map<LocalDate, List<GroupPrayResponseDto>> groupPray;

}
