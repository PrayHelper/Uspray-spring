package com.uspray.uspray.DTO.grouppray;

import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.ScrapAndHeart;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Base64;
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
        this.content = new String(Base64.getDecoder().decode(groupPray.getContent()));
        this.authorName = member.getName();
        setIsOwner(groupPray.getAuthor().getId(), member.getId());
        setSC(scrapAndHeart);
        this.createdAt = LocalDate.from(groupPray.getCreatedAt());
    }

    private void setSC(ScrapAndHeart scrapAndHeart) {
        if (scrapAndHeart != null) {
            this.heart = scrapAndHeart.isHeart();
            this.scrap = scrapAndHeart.isScrap();
        }
    }

    private void setIsOwner(Long authorId, Long memberId) {
        this.isOwner = Objects.equals(authorId, memberId);
    }

}
