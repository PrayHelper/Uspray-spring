package com.uspray.uspray.domain.group.repository;

import com.uspray.uspray.domain.group.model.GroupPray;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.group.model.ScrapAndHeart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScrapAndHeartRepository extends JpaRepository<ScrapAndHeart, Long> {

    Optional<ScrapAndHeart> findScrapAndHeartByGroupPrayAndMember(GroupPray groupPray,
        Member member);

    @Query("select count(h) from ScrapAndHeart h where h.groupPray in :groupPray and h.heart = :trigger")
    Long countHeart(@Param("groupPray") List<GroupPray> groupPray,
        @Param("trigger") Boolean trigger);

    ScrapAndHeart findByMemberAndSharedPray(Member member, Pray sharedPray);
}
