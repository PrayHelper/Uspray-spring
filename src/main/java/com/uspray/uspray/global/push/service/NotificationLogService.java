package com.uspray.uspray.global.push.service;

import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.global.push.model.NotificationLog;
import com.uspray.uspray.global.push.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationLogService {

	private final NotificationLogRepository notificationLogRepository;
	private final FCMNotificationService fcmNotificationService;

	public void sendNotification(Member member) {
		try {
			fcmNotificationService.sendMessageTo(
				member.getFirebaseToken(),
				"💘",
				"누군가가 당신의 기도제목을 두고 기도했어요");
		} catch (Exception e) {
			log.error(e.getMessage());

		}
		log.error(
			"send notification to " + member
		);
	}

	@Transactional
	public void saveNotificationLog(Pray pray, Member member) {
		NotificationLog notificationLog = NotificationLog.builder()
			.pray(pray)
			.member(member)
			.title("누군가가 당신의 기도제목을 두고 기도했어요")
			.build();

		notificationLogRepository.save(notificationLog);
	}

}
