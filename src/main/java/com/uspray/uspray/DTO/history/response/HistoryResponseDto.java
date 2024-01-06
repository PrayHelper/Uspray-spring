package com.uspray.uspray.DTO.history.response;

import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Pray;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "히스토리 응답 DTO")
public class HistoryResponseDto {

    private Long historyId;

    private String userId;

    private String name;

    private String content;

    private LocalDate deadline;

    private LocalDateTime createdAt;

    private Long categoryId;

    public static HistoryResponseDto of(History history) {
        return new HistoryResponseDto(history.getId(), history.getMember().getUserId(),
            history.getMember().getName(), history.getContent(), history.getDeadline(), history.getCreatedAt(), history.getCategoryId());
    }

    public static HistoryResponseDto shared(History history, Pray originPray) {
        return new HistoryResponseDto(history.getId(), originPray.getMember().getUserId(),
            originPray.getMember().getName(), history.getContent(), history.getDeadline(), history.getCreatedAt(), history.getCategoryId());
    }

}
