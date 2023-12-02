package com.uspray.uspray.DTO.grouppray;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Data;

@Data
public class GroupPrayResponseDto {

    @Schema(example = "1")
    private Long groupPrayId;
    @Schema(example = "테스트이기도 아니기도")
    private String content;
    @Schema(example = "오리너구리")
    private String authorName;
    @Schema(example = "true")
    private boolean isOwner;
    @Schema(example = "true")
    private boolean heart;
    @Schema(example = "false")
    private boolean scrap;
    @Schema(example = "1592-07-17")
    private LocalDate createdAt;

    @QueryProjection
    public GroupPrayResponseDto(Long groupPrayId, String content, String authorName, Long authorId,
        Long memberId, boolean heart, boolean scrap, LocalDateTime createdAt) {
        this.groupPrayId = groupPrayId;
        this.content = content;
        this.authorName = authorName;
        setIsOwner(authorId, memberId);
        this.heart = heart;
        this.scrap = scrap;
        this.createdAt = LocalDate.from(createdAt);
    }

    private void setIsOwner(Long authorId, Long memberId) {
        this.isOwner = Objects.equals(authorId, memberId);
    }

}
