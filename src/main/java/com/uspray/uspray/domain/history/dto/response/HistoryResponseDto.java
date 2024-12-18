package com.uspray.uspray.domain.history.dto.response;

import com.uspray.uspray.domain.history.model.History;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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

	private LocalDate createdAt;

	private LocalDate deadline;

	private Long categoryId;

	private Boolean canEdit;

	public static HistoryResponseDto of(History history) {
		return new HistoryResponseDto(history.getId(), history.getMember().getUserId(),
			history.getMember().getName(), history.getContent(), history.getStartDate(),
			history.getDeadline(), history.getCategoryId(),
			history.getCategoryType() == CategoryType.PERSONAL && !history.getIsShared());
	}

	public static HistoryResponseDto shared(History history, Member originMember) {
		return new HistoryResponseDto(history.getId(), originMember.getUserId(),
			originMember.getName(), history.getContent(), history.getStartDate(),
			history.getDeadline(),
			history.getCategoryId(), false); // 공유받은 기도제목은 수정 불가능, 항상 false
	}

}
