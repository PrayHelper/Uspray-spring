package com.uspray.uspray.DTO.history.response;

import com.uspray.uspray.domain.History;
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

    public static HistoryDetailResponseDto of(History history) {
        return new HistoryDetailResponseDto(history.getId(), history.getMember().getUserId(),
            history.getContent(), history.getPersonalCount(), history.getTotalCount(),
            history.getDeadline(),
            history.getCreatedAt().toLocalDate(), history.getCategoryId());
    }
}
