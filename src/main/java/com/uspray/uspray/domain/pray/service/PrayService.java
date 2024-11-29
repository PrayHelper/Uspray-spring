package com.uspray.uspray.domain.pray.service;

import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayService {

	private final PrayRepository prayRepository;

	@Transactional
	public PrayResponseDto getPrayDetail(Long prayId, String username) {
		Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
		return PrayResponseDto.of(pray);
	}

	public PrayResponseDto savePray(Pray pray) {
		return PrayResponseDto.of(prayRepository.save(pray));
	}

	public Pray findPrayById(Long prayId) {
		return prayRepository.getPrayById(prayId);
	}

	public List<Pray> findAllByIdIn(List<Long> prayIds) {
		return prayRepository.findAllByIdIn(prayIds);
	}

	public List<Pray> findAllByOriginPrayIdIn(List<Long> prayIds) {
		return prayRepository.findAllByOriginPrayIdIn(prayIds);
	}

	public List<Pray> getPrayListByCategory(Category category) {
		return prayRepository.findAllByCategory(category);
	}

	public List<Pray> getPrayListByMemberAndCategory(Member member, Category category) {
		return prayRepository.findAllByMemberAndCategoryOrderByCreatedAtAsc(member, category);
	}

	public Pray getPrayByIdAndMemberId(Long prayId, String username) {
		return prayRepository.getPrayByIdAndMemberId(prayId, username);
	}

	private Pray getSharedPray(Long prayId) {
		return prayRepository.getPrayByOriginPrayId(prayId);
	}

	public List<Pray> getPrayListDeadlineBefore(LocalDate currentDate) {
		return prayRepository.findAllByDeadlineBefore(currentDate);
	}

	public Integer getSharedCountByIdAndOriginPrayId(Long prayId, Long originPrayId) {
		return prayRepository.getSharedCountByIdAndOriginPrayId(prayId, originPrayId);
	}

	public void deletePray(Pray pray) {
		prayRepository.delete(pray);
	}

	public Pray cancelPray(Long prayId, String username) {
		return prayRepository.cancelPray(prayId, username);
	}

	public Boolean isSharedPray(Pray pray) {
		return getSharedPray(pray.getId()) == null;
	}

	public void checkIsAlreadyPrayed(Pray pray) {
		if (pray.getLastPrayedAt().equals(LocalDate.now())) {
			throw new NotFoundException(ErrorStatus.ALREADY_PRAYED_TODAY);
		}
	}
}
