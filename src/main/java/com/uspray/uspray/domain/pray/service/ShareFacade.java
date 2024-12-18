package com.uspray.uspray.domain.pray.service;

import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.category.repository.CategoryRepository;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.member.repository.MemberRepository;
import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPrayDeleteRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPrayRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.request.SharedPraySaveRequestDto;
import com.uspray.uspray.domain.pray.dto.sharedpray.response.SharedPrayResponseDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.pray.model.SharedPray;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
import com.uspray.uspray.domain.pray.repository.SharedPrayRepository;
import com.uspray.uspray.global.enums.CategoryType;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.global.push.model.NotificationLog;
import com.uspray.uspray.global.push.repository.NotificationLogRepository;
import com.uspray.uspray.global.push.service.FCMNotificationService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareFacade {

	private final SharedPrayRepository sharedPrayRepository;
	private final MemberRepository memberRepository;
	private final PrayRepository prayRepository;
	private final CategoryRepository categoryRepository;
	private final NotificationLogRepository notificationLogRepository;
	private final FCMNotificationService fcmNotificationService;

	@Transactional(readOnly = true)
	public List<SharedPrayResponseDto> getSharedPrayList(String username) {

		Member member = memberRepository.getMemberByUserId(username);
		List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByMemberOrderByCreatedAtDesc(
			member);

		List<SharedPrayResponseDto> result = new ArrayList<>();

		for (SharedPray sharedPray : sharedPrayList) {
			Pray pray = prayRepository.getPrayByIdIgnoreDelete(
				sharedPray.getPray().getId());
			result.add(SharedPrayResponseDto.of(sharedPray, pray));
		}

		return result;
	}

	@Transactional
	public Long receivedSharedPray(String username, SharedPrayRequestDto sharedPrayRequestDto) {

		Member member = memberRepository.getMemberByUserId(username);
		List<Pray> prayList = prayRepository.findAllByIdIn(sharedPrayRequestDto.getPrayIds());
		long total = 0L;

		if (prayList.size() != sharedPrayRequestDto.getPrayIds().size()) {
			throw new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION);
		}
		for (Pray pray : prayList) {
			if (!receiveCheck(member, pray)) {
				continue;
			}
			SharedPray sharedPray = SharedPray.builder()
				.member(member)
				.pray(pray)
				.build();
			sharedPrayRepository.save(sharedPray);
			// 기도제목을 보관함에 담으면 원래 기도제목의 isShared를 true로 변경
			pray.setIsShared();
			prayRepository.save(pray);
			total++;
		}

		if (total == 0L) {
			throw new CustomException(ErrorStatus.CANNOT_RECEIVE_SHARED_PRAY_EXCEPTION);
		}
		return total;
	}

	private boolean receiveCheck(Member member, Pray pray) {
		if (pray.getMember().equals(member)) {
			// 자기 자신의 기도제목은 보관함에 넣을 수 없음
			return false;
		}
		// 이미 저장된 기도제목의 경우 다시 보관함에 담기지 않도록 처리
		if (prayRepository.existsByMemberAndOriginPrayId(member, pray.getId())) {
			return false;
		}
		// 이미 보관함에 있는 기도제목은 보관함에 넣을 수 없음
		return !sharedPrayRepository.existsByMemberAndPray(member, pray);
	}

	@Transactional
	public void saveSharedPray(String username, SharedPraySaveRequestDto sharedPraySaveRequestDto) {

		Member member = memberRepository.getMemberByUserId(username);
		Category category = categoryRepository.getCategoryById(
			sharedPraySaveRequestDto.getCategoryId());
		if (!category.getCategoryType().equals(CategoryType.SHARED)) {
			throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH);
		}
		List<Long> sharedPrayIds = sharedPraySaveRequestDto.getSharedPrayIds();

		for (Long id : sharedPrayIds) {
			save(member, id, category, sharedPraySaveRequestDto.getDeadline());
		}
	}

	private void save(Member member, Long sharedPrayId, Category category, LocalDate deadline) {

		SharedPray sharedPray = sharedPrayRepository.getSharedPrayById(sharedPrayId);

		if (sharedPray.getPray().getDeleted()) {
			sharedPrayRepository.deleteById(sharedPrayId);
			throw new CustomException(ErrorStatus.PRAY_ALREADY_DELETED_EXCEPTION);
		}
		Pray pray = Pray.builder()
			.member(member)
			.content(new String(Base64.getDecoder().decode(sharedPray.getPray().getContent())))
			.deadline(deadline)
			.originPrayId(sharedPray.getPray().getId())
			.originMemberId(sharedPray.getPray().getMember().getId()) // 원래 기도제목의 원자자가 되어야 함
			.category(category)
			.categoryType(CategoryType.SHARED)
			.startDate(LocalDate.now())
			.build();
		prayRepository.save(pray);
		Pray originPray = prayRepository.getPrayById(pray.getOriginPrayId());
		if (originPray.getMember().getThirdNotiAgree()) {
			sendNotificationAndSaveLog(originPray, originPray.getMember());
		}
		sharedPrayRepository.deleteById(sharedPrayId);
	}

	public void sendNotificationAndSaveLog(Pray pray, Member member) {
		try {
			fcmNotificationService.sendMessageTo(
				member.getFirebaseToken(),
				"💌",
				"누군가가 당신의 기도제목을 저장했어요");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.error(
			"send notification to " + memberRepository.getMemberByUserId(member.getUserId())
		);
		NotificationLog notificationLog = NotificationLog.builder()
			.pray(pray)
			.member(memberRepository.getMemberByUserId(member.getUserId()))
			.title("누군가가 당신의 기도제목을 저장했어요")
			.build();
		notificationLogRepository.save(notificationLog);
	}

	@Transactional
	public void deleteSharedPray(String username,
		SharedPrayDeleteRequestDto sharedPrayDeleteRequestDto) {

		Member member = memberRepository.getMemberByUserId(username);
		List<Long> sharedPrayIds = sharedPrayDeleteRequestDto.getSharedPrayIds();

		for (Long id : sharedPrayIds) {
			delete(member, id);
		}
	}

	private void delete(Member member, Long sharedPrayId) {

		SharedPray sharedPray = sharedPrayRepository.getSharedPrayById(sharedPrayId);

		if (!sharedPray.getMember().equals(member)) {
			throw new CustomException(ErrorStatus.DELETE_NOT_AUTHORIZED_EXCEPTION);
		}
		sharedPrayRepository.deleteById(sharedPrayId);
	}
}
