package com.uspray.uspray.DTO.grouppray;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Data;

@Data
public class GroupPrayResponseDto {

    private Long groupPrayId;
    private String content;
    private String authorName;
    private Boolean isOwner;
    private boolean heart;
    private boolean scrap;
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
