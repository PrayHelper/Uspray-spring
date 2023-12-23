package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.ScrapAndHeart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScrapAndHeartRepository extends JpaRepository<ScrapAndHeart, Long> {

    Optional<ScrapAndHeart> findScrapAndHeartByGroupPrayAndMember(GroupPray groupPray, Member member);

    @Query("select count(h) from ScrapAndHeart h where h.groupPray in :groupPray and h.heart = :trigger")
    Long countHeart(@Param("groupPray") List<GroupPray> groupPray, @Param("trigger") Boolean trigger);

}
