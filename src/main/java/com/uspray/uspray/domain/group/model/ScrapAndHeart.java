package com.uspray.uspray.domain.group.model;


import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapAndHeart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_heart_id")
    private Long id;

    private Boolean heart = false;
    private Boolean scrap = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grouppray_id")
    private GroupPray groupPray;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "pray_id")
    private Pray sharedPray;

    @Builder
    public ScrapAndHeart(GroupPray groupPray, Member member, Pray sharedPray, Boolean heart, Boolean scrap) {
        setGroupPray(groupPray);
        setMember(member);
        setSharedPray(sharedPray);
        this.heart = heart;
        this.scrap = scrap;
    }

    public static ScrapAndHeart createdByGroupPrayOf(GroupPray groupPray, Member member) {
        return ScrapAndHeart.builder()
            .groupPray(groupPray)
            .member(member)
            .build();
    }

    public static ScrapAndHeart createdByScrapOf(GroupPray groupPray, Member member, Pray sharedPray) {
        return ScrapAndHeart.builder()
            .groupPray(groupPray)
            .member(member)
            .sharedPray(sharedPray)
            .scrap(true)
            .build();
    }

    private void setGroupPray(GroupPray groupPray) {
        this.groupPray = groupPray;
        groupPray.getScrapAndHeart().add(this);
    }

    private void setMember(Member member) {
        this.member = member;
    }

    private void setSharedPray(Pray pray) {
        this.sharedPray = pray;
    }

    public void heartPray() {
        this.heart = true;
    }

    public void scrapPray(Pray pray) {
        this.scrap = true;
        this.sharedPray = pray;
    }

    public void deletePrayId() {
        this.sharedPray = null;
        this.scrap = false;
    }
}
