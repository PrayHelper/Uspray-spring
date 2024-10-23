package com.uspray.uspray.global.common.service;

import com.uspray.uspray.global.enums.NotificationType;
import com.uspray.uspray.domain.member.repository.MemberRepository;
import com.uspray.uspray.domain.pray.service.PrayFacade;
import com.uspray.uspray.domain.pray.service.ShareService;
import com.uspray.uspray.global.push.service.FCMNotificationService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final MemberRepository memberRepository;
    private final FCMNotificationService fcmNotificationService;
    private final ShareService shareService;
    private final PrayFacade prayFacadeService;

    @Scheduled(cron = "0 0 8 * * *")
    public void pushPrayNotification() throws IOException {
        List<String> deviceTokens = memberRepository.getDeviceTokensByFirstNotiAgree(
            true);
        for (String device : deviceTokens) {
            fcmNotificationService.sendMessageTo(device, NotificationType.PRAY_TIME.getTitle(),
                NotificationType.PRAY_TIME.getBody());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanSharedPray() {
        LocalDate thresholdDate = LocalDate.now().minusDays(15);
        shareService.cleanSharedPray(thresholdDate);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void convertPrayToHistory() {
        prayFacadeService.moveExpiredPrayersToHistory();
    }
}
