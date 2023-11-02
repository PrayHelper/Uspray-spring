package com.uspray.uspray.DTO.clubpray;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ClubPrayResponseDto {

    private Long clubPrayId;
    private String content;
    private Boolean isOwner = false;
    private Long authorId;
    private Integer liked;

    @QueryProjection
    public ClubPrayResponseDto(Long clubPrayId, String content, Long authorId, Integer liked) {
        this.clubPrayId = clubPrayId;
        this.content = content;
        this.authorId = authorId;
        this.liked = liked;
    }

    public void changeOwner() {
        this.isOwner = true;
    }

}
