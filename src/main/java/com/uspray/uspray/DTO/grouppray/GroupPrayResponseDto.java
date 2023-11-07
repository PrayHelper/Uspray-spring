package com.uspray.uspray.DTO.grouppray;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GroupPrayResponseDto {

    private Long groupPrayId;
    private String content;
    private Boolean isOwner = false;
    private Long authorId;
    private Integer liked;

    @QueryProjection
    public GroupPrayResponseDto(Long groupPrayId, String content, Long authorId, Integer liked) {
        this.groupPrayId = groupPrayId;
        this.content = content;
        this.authorId = authorId;
        this.liked = liked;
    }

    public void changeOwner() {
        this.isOwner = true;
    }

}
