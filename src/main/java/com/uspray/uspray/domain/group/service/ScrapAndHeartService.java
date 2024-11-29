package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.model.GroupPray;
import com.uspray.uspray.domain.group.model.ScrapAndHeart;
import com.uspray.uspray.domain.group.repository.ScrapAndHeartRepository;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapAndHeartService {
	private final ScrapAndHeartRepository scrapAndHeartRepository;

	public void deleteScrapAndHeart(Member member, Pray pray) {
		ScrapAndHeart scrapAndHeart = scrapAndHeartRepository.findByMemberAndSharedPray(member, pray);
		if (scrapAndHeart != null) {
			scrapAndHeart.deletePrayId();
		}
	}

	@Transactional
	public void save(ScrapAndHeart scrapAndHeart) {
		scrapAndHeartRepository.save(scrapAndHeart);
	}

	public Long countHeart(List<GroupPray> groupPrayList) {
		return scrapAndHeartRepository.countHeart(groupPrayList, true);
	}

	public Optional<ScrapAndHeart> findScrapAndHeartByGroupPrayAndMember(GroupPray groupPray, Member member) {
		return scrapAndHeartRepository.findScrapAndHeartByGroupPrayAndMember(groupPray, member);
	}
}
