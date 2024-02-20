package com.uspray.uspray.DTO.history.response;

import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Pray;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDetailResponseDto {

    private Long historyId;

    private String name;

    private String content;

    private Integer personal_count;

    private Integer total_count;

    private LocalDate deadline;

    private LocalDate createdAt;

    private Long categoryId;

    private Boolean canEdit;

    public static HistoryDetailResponseDto of(History history) {
        return new HistoryDetailResponseDto(history.getId(), history.getMember().getName(),
            history.getContent(), history.getPersonalCount(), history.getTotalCount(),
            history.getDeadline(),
            history.getCreatedAt().toLocalDate(), history.getCategoryId(),
            history.getPrayType() == PrayType.PERSONAL && !history.getIsShared());
    }

    public static HistoryDetailResponseDto shared(History history, Pray originPray) {
        return new HistoryDetailResponseDto(history.getId(), originPray.getMember().getName(),
            history.getContent(), history.getPersonalCount(), history.getTotalCount(),
            history.getDeadline(),
            history.getCreatedAt().toLocalDate(), history.getCategoryId(),
            history.getPrayType() == PrayType.PERSONAL && !history.getIsShared());
    }
}
