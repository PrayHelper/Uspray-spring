package com.uspray.uspray.domain.pray.service;

import com.uspray.uspray.domain.category.dto.CategoryResponseDto;
import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.category.service.CategoryService;
import com.uspray.uspray.domain.group.service.ScrapAndHeartService;
import com.uspray.uspray.domain.history.service.HistoryService;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.member.service.MemberService;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.global.enums.CategoryType;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.global.push.service.NotificationLogService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayFacade {

	private final HistoryService historyService;
	private final NotificationLogService notificationLogService;
	private final ScrapAndHeartService scrapAndHeartService;
	private final ShareService shareService;
	private final PrayService prayService;
	private final MemberService memberService;
	private final CategoryService categoryService;

	private void checkIsAlreadyPrayed(Pray pray) {
		if (pray.getLastPrayedAt().equals(LocalDate.now())) {
			throw new NotFoundException(ErrorStatus.ALREADY_PRAYED_TODAY);
		}
	}

	private boolean isSameCategory(Pray pray, Category category) {
		return pray.getCategoryType() == category.getCategoryType();
	}

	private boolean isSharedPray(Pray pray) {
		return prayService.isSharedPray(pray) || pray.getCategoryType() == CategoryType.SHARED;
	}

	private void checkSharedPrayValidation(PrayUpdateRequestDto prayUpdateRequestDto,
		Pray pray) {
		if (isPrayUpdatable(pray, prayUpdateRequestDto.getContent())) {
			throw new CustomException(ErrorStatus.SHARED_PRAY_UPDATE_EXCEPTION);
		}
	}

	private Boolean isPrayUpdatable(Pray pray, String content) {
		return isSharedPray(pray) && content != null;
	}

	@Transactional
	public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username,
		LocalDate startDateOrNull) {
		Member member = memberService.findMemberByUserId(username);
		Category category = categoryService.getCategoryByIdAndMemberAndType(
			prayRequestDto.getCategoryId(),
			member, CategoryType.PERSONAL);
		return prayService.savePray(prayRequestDto.toEntity(member, category, startDateOrNull));
	}

	@Transactional
	public PrayResponseDto updatePray(Long prayId, String username,
		PrayUpdateRequestDto prayUpdateRequestDto) {
		Pray pray = prayService.getPrayByIdAndMemberId(prayId, username);

		checkSharedPrayValidation(prayUpdateRequestDto, pray);

		Category category = getCategory(prayUpdateRequestDto, pray);

		return PrayResponseDto.of(pray.update(prayUpdateRequestDto, category));
	}

	@NotNull
	private Category getCategory(PrayUpdateRequestDto prayUpdateRequestDto, Pray pray) {
		Category category = categoryService.getCategoryByIdAndMemberAndType(
			prayUpdateRequestDto.getCategoryId(),
			pray.getMember(),
			CategoryType.PERSONAL);
		// 기도 제목 타입과 카테고리 타입 일치하는 지 확인
		if (!isSameCategory(pray, category)) {
			throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH);
		}
		return category;
	}

	@Transactional
	public void moveExpiredPrayersToHistory() {
		List<Pray> prayList = prayService.getPrayListDeadlineBefore(LocalDate.now());
		for (Pray pray : prayList) {
			convertPrayToHistory(pray);
		}
	}

	private void convertPrayToHistory(Pray pray) {
		pray.complete();
		Integer sharedCount = prayService.getSharedCountByOriginPrayId(
			pray.getOriginPrayId());
		historyService.createHistory(pray, sharedCount);
		prayService.deletePray(pray);
	}

	@Transactional
	public CategoryResponseDto deleteCategory(Long categoryId) {
		Category category = categoryService.getCategoryById(categoryId);

		prayService.getPrayListByCategory(category).forEach(this::convertPrayToHistory);

		return categoryService.deleteCategory(category);
	}

	@Transactional
	public List<PrayListResponseDto> todayPray(Long prayId, String username) {
		Pray pray = prayService.getPrayByIdAndMemberId(prayId, username);
		handlePrayedToday(pray);
		return getPrayList(username, pray.getCategoryType().stringValue());
	}


	private void handlePrayedToday(Pray pray) {
		checkIsAlreadyPrayed(pray);
		pray.countUp();

		if (pray.getCategoryType() == CategoryType.SHARED) {
			Member originMember = memberService.findMemberById(pray.getOriginMemberId());
			if (originMember.getSecondNotiAgree()) {
				notificationLogService.sendNotification(originMember);
				notificationLogService.saveNotificationLog(pray, originMember);
			}
		}
	}

	@Transactional
	public PrayResponseDto deletePray(Long prayId, String username) {
		Pray pray = prayService.getPrayByIdAndMemberId(prayId, username);
		Member member = memberService.findMemberByUserId(username);

		scrapAndHeartService.deleteScrapAndHeart(member, pray);
		shareService.deleteByOriginPray(pray);
		prayService.deletePray(pray);
		return PrayResponseDto.of(pray);
	}

	@Transactional
	public List<PrayListResponseDto> getPrayList(String username, String prayType) {
		Member member = memberService.findMemberByUserId(username);
		List<Category> categoryList = getCategories(
			prayType, member);

		if (categoryList.isEmpty()) {
			return Collections.emptyList();
		}

		return convertPrayListResponseDtoListByCategory(categoryList, member);
	}

	@NotNull
	private List<PrayListResponseDto> convertPrayListResponseDtoListByCategory(
		List<Category> categoryList, Member member) {
		return categoryList.stream()
			.map(category -> createPrayListResponseDto(member, category))
			.collect(Collectors.toList());
	}

	private List<Category> getCategories(String prayType, Member member) {
		CategoryType categoryType = CategoryType.valueOf(prayType);
		return categoryService.getCategoryListByMemberAndCategoryType(member,
			categoryType);
	}

	private PrayListResponseDto createPrayListResponseDto(Member member, Category category) {
		// 기도 리스트 변환
		List<PrayResponseDto> prayResponseDtos = prayService.getPrayListByMemberAndCategory(member,
				category).stream()
			.map(this::convertToPrayResponseDto)
			.collect(Collectors.toList());

		// PrayListResponseDto 생성
		return new PrayListResponseDto(category.getId(), category.getName(), category.getColor(),
			prayResponseDtos);
	}

	private PrayResponseDto convertToPrayResponseDto(Pray pray) {
		// 기도의 타입에 따라 적절한 PrayResponseDto 생성
		if (pray.getCategoryType() == CategoryType.SHARED) {
			Member originMember = memberService.findMemberById(pray.getOriginMemberId());
			return PrayResponseDto.shared(pray, originMember);
		} else {
			return PrayResponseDto.of(pray);
		}
	}


	@Transactional
	public List<PrayListResponseDto> completePray(Long prayId, String username) {
		Pray pray = prayService.getPrayByIdAndMemberId(prayId, username);
		convertPrayToHistory(pray);

		return getPrayList(username, pray.getCategoryType().stringValue());
	}


	@Transactional
	public List<PrayListResponseDto> cancelPray(Long prayId, String username) {
		return getPrayList(username,
			prayService.cancelPray(prayId, username).getCategoryType().stringValue());
	}
}
