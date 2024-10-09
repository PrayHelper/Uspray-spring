package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.model.ScrapAndHeart;
import com.uspray.uspray.domain.group.repository.ScrapAndHeartRepository;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
