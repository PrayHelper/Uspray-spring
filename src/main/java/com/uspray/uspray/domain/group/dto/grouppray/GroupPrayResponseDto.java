package com.uspray.uspray.domain.group.dto.grouppray;

import com.uspray.uspray.domain.group.model.GroupPray;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.group.model.ScrapAndHeart;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Builder;
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
    private boolean heart = false;
    @Schema(example = "false")
    private boolean scrap = false;
    @Schema(example = "1592-07-17")
    private LocalDate createdAt;

    @Builder
    public GroupPrayResponseDto(GroupPray groupPray, Member member, ScrapAndHeart scrapAndHeart) {
        this.groupPrayId = groupPray.getId();
        this.content = groupPray.getContent();
        this.authorName = groupPray.getAuthor().getName();
        setIsOwner(groupPray.getAuthor().getId(), member.getId());
        setSC(scrapAndHeart);
        this.createdAt = LocalDate.from(groupPray.getCreatedAt());
    }

    private void setSC(ScrapAndHeart scrapAndHeart) {
        if (scrapAndHeart != null) {
            this.heart = scrapAndHeart.getHeart();
            this.scrap = scrapAndHeart.getScrap();
        }
    }

    private void setIsOwner(Long authorId, Long memberId) {
        this.isOwner = Objects.equals(authorId, memberId);
    }

}
