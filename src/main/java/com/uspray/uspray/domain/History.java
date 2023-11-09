package com.uspray.uspray.domain;

import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.common.domain.AuditingTimeEntity;
import java.time.LocalDate;
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

    private Integer count;

    private LocalDate deadline;

    @Column(name = "origin_pray_id")
    private Long originPrayId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PrayType prayType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public History(Pray pray) {
        this.member = pray.getMember();
        this.content = pray.getContent();
        this.count = pray.getCount();
        this.deadline = pray.getDeadline();
        this.originPrayId = pray.getOriginPrayId();
        this.prayType = pray.getPrayType();
        this.category = pray.getCategory();
    }
}