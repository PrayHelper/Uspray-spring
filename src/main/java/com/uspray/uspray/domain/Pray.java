package com.uspray.uspray.domain;

import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.common.domain.AuditingTimeEntity;
import java.time.LocalDate;
import java.util.Base64;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE pray SET deleted = true WHERE pray_id = ?")
@Where(clause = "deleted=false")
public class Pray extends AuditingTimeEntity {
    
    private final Boolean deleted = false;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pray_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    private String content;
    private Integer count;
    private LocalDate deadline;
    private Boolean isShared = false;
    
    @Column(name = "origin_pray_id")
    private Long originPrayId;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private PrayType prayType;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "group_pray_id")
    private GroupPray groupPray;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    private LocalDate lastPrayedAt;
    
    @Builder
    public Pray(Member member, String content, LocalDate deadline, Long originPrayId,
        Category category, PrayType prayType) {
        this.member = member;
        this.content = new String(Base64.getEncoder().encode(content.getBytes()));
        this.count = 0;
        this.deadline = deadline;
        this.originPrayId = originPrayId;
        this.isShared = (originPrayId != null);
        this.category = category;
        this.prayType = prayType;
    }
    
    public void update(PrayRequestDto prayRequestDto) {
        this.content = prayRequestDto.getContent();
        this.deadline = prayRequestDto.getDeadline();
    }
    
    public String getContent() {
        return new String(Base64.getDecoder().decode(content));
    }
    
    public void countUp() {
        this.count++;
        this.lastPrayedAt = LocalDate.now();
    }
    
    public void complete() {
        this.deadline = LocalDate.now();
    }
}
