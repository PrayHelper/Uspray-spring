package com.uspray.uspray.domain.history.model;

import com.uspray.uspray.global.enums.PrayType;
import com.uspray.uspray.global.common.model.AuditingTimeEntity;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import java.time.LocalDate;
import java.util.Base64;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class History extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String content;

    private Integer personalCount;

    private Integer totalCount;

    private LocalDate startDate;
    private LocalDate deadline;

    @Column(name = "origin_pray_id")
    private Long originPrayId;

    @Column(name = "origin_member_id")
    private Long originMemberId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PrayType prayType;

    private Long categoryId;

    private Boolean isShared;

    @Builder
    public History(Pray pray, Integer totalCount) {
        this.member = pray.getMember();
        this.content = pray.getContent();
        this.personalCount = pray.getCount();
        this.totalCount = (totalCount == null) ? 0 : totalCount; // totalCount에 기본값 설정
        this.startDate = LocalDate.from(pray.getStartDate());
        this.deadline = pray.getDeadline();
        this.originPrayId = pray.getOriginPrayId();
        this.originMemberId = pray.getOriginMemberId();
        this.prayType = pray.getPrayType();
        this.categoryId = pray.getCategory().getId();
        this.isShared = pray.getIsShared();
    }

    public String getContent() {
        return new String(Base64.getDecoder().decode(content));
    }
}