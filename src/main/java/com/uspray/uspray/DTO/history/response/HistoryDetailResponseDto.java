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

    private String userId;

    private String content;

    private Integer count;

    private LocalDate deadline;

    private LocalDate createdAt;

    public static HistoryDetailResponseDto of(History history) {
        return new HistoryDetailResponseDto(history.getId(), history.getMember().getUserId(),
            history.getContent(), history.getCount(), history.getDeadline(), history.getCreatedAt().toLocalDate());
    }
}
