package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.NotificationLog;
import com.uspray.uspray.domain.NotificationLogId;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.NotificationLogRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayService {

    private final PrayRepository prayRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PrayResponseDto getPrayDetail(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public PrayResponseDto deletePray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        prayRepository.delete(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public List<PrayListResponseDto> getPrayList(String username, String prayType) {

        List<Pray> prays = prayRepository.findAllWithOrderAndType(username, prayType);

        // Pray 엔티티를 categoryId를 기준으로 그룹화한 맵 생성
        Map<Long, List<Pray>> prayMap = prays.stream()
            .collect(Collectors.groupingBy(pray -> pray.getCategory().getId()));

        // 그룹화된 맵을 PrayListResponseDto 변환하여 반환
        return prayMap.entrySet().stream()
            .map(entry -> new PrayListResponseDto(entry.getKey(),
                entry.getValue().get(0).getCategory().getName(),
                entry.getValue().stream()
                    .map(PrayResponseDto::of)
                    .collect(Collectors.toList())))
            .collect(Collectors.toList());
    }

    @Transactional
    public List<PrayListResponseDto> todayPray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        LocalDate today = LocalDate.now();
        if (isAlreadyPrayedToday(pray, today)) {
            handleAlreadyPrayedToday(pray);
        }
        handleNotPrayedToday(pray);
        return getPrayList(username, PrayType.PERSONAL.stringValue());
    }

    private void sendNotificationAndSaveLog(Pray pray, String username) {
        // TODO: notification 보내는 로직 추가
        System.out.println("send notification to " + memberRepository.getMemberByUserId(username));
        NotificationLogId notificationLogId = NotificationLogId.builder()
            .pray(pray)
            .member(memberRepository.getMemberByUserId(username))
            .build();
        NotificationLog notificationLog = NotificationLog.builder()
            .id(notificationLogId)
            .build();
        notificationLogRepository.save(notificationLog);
    }

    private boolean isAlreadyPrayedToday(Pray pray, LocalDate today) {
        return pray.getLastPrayedAt() != null && pray.getLastPrayedAt().equals(today);
    }

    private void handleAlreadyPrayedToday(Pray pray) {
        throw new NotFoundException(ErrorStatus.ALREADY_PRAYED_TODAY,
            ErrorStatus.ALREADY_PRAYED_TODAY.getMessage());
    }

    private void handleNotPrayedToday(Pray pray) {
        pray.countUp();

        if (pray.getPrayType() == PrayType.SHARED) {
            Pray originPray = prayRepository.getPrayById(pray.getOriginPrayId());
            System.out.println(originPray.getMember().getName());
            System.out.println(pray.getMember().getName());
            sendNotificationAndSaveLog(pray, originPray.getMember().getUserId());
        }
    }

    @Transactional
    public List<PrayListResponseDto> completePray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        prayRepository.delete(pray);

        return getPrayList(username, pray.getPrayType().stringValue());
    }
}
