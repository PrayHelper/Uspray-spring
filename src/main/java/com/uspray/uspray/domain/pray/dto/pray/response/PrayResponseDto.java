package com.uspray.uspray.domain.pray.dto.pray.response;

import com.querydsl.core.annotations.QueryProjection;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Base64;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "기도제목 응답 DTO")
public class PrayResponseDto {

    @Schema(description = "기도제목 id", example = "3")
    private Long prayId;

    @NotNull
    @Schema(description = "기도제목 내용", example = "@@이가 $$ 할 수 있도록")
    private String content;

    @NotNull
    @Schema(description = "기도제목 작성자 이름", example = "홍길동")
    private String name;

    @NotNull
    @Schema(description = "기도제목 마감일", example = "2025-01-01")
    private LocalDate deadline;

    @Schema(description = "기도제목 카테고리", example = "1")
    private Long categoryId;

    @Schema(description = "기도제목 카테고리 이름", example = "카테고리 이름")
    private String categoryName;

    @Schema(description = "오늘 기도 여부", example = "true")
    private Boolean isPrayedToday;

    @Schema(description = "기도제목 공유 여부", example = "true")
    private Boolean isShared;

    @Schema(description = "모임 기도제목 존재 여부", example = "false")
    private Boolean inGroup;

    @QueryProjection
    public PrayResponseDto(Long prayId, String content, String name, LocalDate deadline,
        Long categoryId, String categoryName, LocalDate lastPray, Boolean isShared) {
        this.prayId = prayId;
        this.content = new String(Base64.getDecoder().decode(content));
        this.name = name;
        this.deadline = deadline;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.isPrayedToday = lastPray.isEqual(LocalDate.now());
        this.isShared = isShared;
        this.inGroup = false;
    }


    public static PrayResponseDto of(Pray pray) {
        return new PrayResponseDto(
            pray.getId(),
            pray.getContent(),
            pray.getMember().getName(),
            pray.getDeadline(),
            pray.getCategory().getId(),
            pray.getCategory().getName(),
            pray.getLastPrayedAt(),
            pray.getIsShared());
    }

    public static PrayResponseDto shared(Pray pray, Member originPrayMember) {
        return new PrayResponseDto(
            pray.getId(),
            pray.getContent(),
            originPrayMember.getName(),
            pray.getDeadline(),
            pray.getCategory().getId(),
            pray.getCategory().getName(),
            pray.getLastPrayedAt(),
            pray.getIsShared());
    }

}
