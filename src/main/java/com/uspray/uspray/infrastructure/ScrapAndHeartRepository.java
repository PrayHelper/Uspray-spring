package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.ScrapAndHeart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapAndHeartRepository extends JpaRepository<ScrapAndHeart, Long> {

    Optional<ScrapAndHeart> findScrapAndHeartByGroupPrayAndMember(GroupPray groupPray, Member member);

}
