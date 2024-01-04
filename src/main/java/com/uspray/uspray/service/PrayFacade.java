package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayToGroupPrayDto;
import com.uspray.uspray.DTO.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.NotificationLog;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.ScrapAndHeart;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.GroupPrayRepository;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.infrastructure.HistoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.NotificationLogRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import com.uspray.uspray.infrastructure.ScrapAndHeartRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrayFacade {

    private final MemberRepository memberRepository;
    private final PrayRepository prayRepository;
    private final GroupPrayRepository groupPrayRepository;
    private final GroupRepository groupRepository;
    private final ScrapAndHeartRepository scrapAndHeartRepository;
    private final CategoryRepository categoryRepository;
    private final HistoryRepository historyRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final FCMNotificationService fcmNotificationService;

    @Transactional
    public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username) {
        Member member = memberRepository.getMemberByUserId(username);
        Category category = categoryRepository.getCategoryByIdAndMember(
            prayRequestDto.getCategoryId(),
            member);
        Pray pray = prayRequestDto.toEntity(member, category, PrayType.PERSONAL);
        prayRepository.save(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public PrayResponseDto updatePray(Long prayId, String username,
        PrayUpdateRequestDto prayUpdateRequestDto) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        categoryRepository.getCategoryByIdAndMember(
            prayUpdateRequestDto.getCategoryId(),
            pray.getMember());

        // 그룹 기도 제목은 이 API로 수정 불가능
        checkGroupPray(pray);

        // 이 기도 제목을 공유한 적 없거나, 공유 받은 사람이 없으면 전부 수정 가능
        // 이 기도 제목을 공유한 적 있고, 누구라도 공유 받은 사람이 있으면 기도제목 내용 수정 불가능
        Pray sharedPray = prayRepository.getPrayByOriginPrayId(prayId);
        if (prayUpdateRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.getCategoryByIdAndMember(
                prayUpdateRequestDto.getCategoryId(),
                pray.getMember());
            // 기도 제목 타입과 카테고리 타입 일치하는 지 확인
            if (!pray.getPrayType().toString().equals(category.getCategoryType().toString())) {
                throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH,
                    ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH.getMessage());
            }
            pray.update(prayUpdateRequestDto,
                checkIsShared(sharedPray, pray), category);
        }

        pray.update(prayUpdateRequestDto,
            checkIsShared(sharedPray, pray), null);

        return PrayResponseDto.of(pray);
    }

    public boolean checkIsShared(Pray sharedPray, Pray pray) {
        return sharedPray != null || pray.getPrayType() == PrayType.SHARED;
    }

    public void checkGroupPray(Pray pray) {
        if (pray.getPrayType() == PrayType.GROUP) {
            throw new CustomException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
                ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
        }
    }

    @Transactional
    public void convertPrayToHistory() {
        List<Pray> prayList = prayRepository.findAllByDeadlineBefore(LocalDate.now());
        for (Pray pray : prayList) {
            History history = History.builder()
                .pray(pray)
                .build();
            historyRepository.save(history);
            prayRepository.delete(pray);
        }
    }


    public void createHistory(String username, Long prayId) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        pray.complete();
        History history = History.builder()
            .pray(pray)
            .build();
        historyRepository.save(history);
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
                entry.getValue().get(0).getCategory().getColor(),
                entry.getValue().stream()
                    .map(PrayResponseDto::of)
                    .collect(Collectors.toList())))
            .collect(Collectors.toList());
    }

    @Transactional
    public List<PrayListResponseDto> todayPray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        LocalDate today = LocalDate.now();
        handlePrayedToday(pray, today);
        return getPrayList(username, PrayType.PERSONAL.stringValue());
    }

    private void sendNotificationAndSaveLog(Pray pray, Member member) {
        try {
            fcmNotificationService.sendMessageTo(
                member.getFirebaseToken(),
                "누군가가 당신이 공유한 기도제목을 기도했어요.",
                "💘");
        } catch (Exception e) {
            log.error(e.getMessage());

        }
        log.error(
            "send notification to " + memberRepository.getMemberByUserId(member.getUserId())
        );
        NotificationLog notificationLog = NotificationLog.builder()
            .pray(pray)
            .member(memberRepository.getMemberByUserId(member.getUserId()))
            .title("누군가가 당신이 공유한 기도제목을 기도했어요.")
            .build();
        notificationLogRepository.save(notificationLog);
    }

    private void handlePrayedToday(Pray pray, LocalDate today) {
        if (pray.getLastPrayedAt().equals(today)) {
            throw new NotFoundException(ErrorStatus.ALREADY_PRAYED_TODAY,
                ErrorStatus.ALREADY_PRAYED_TODAY.getMessage());
        }
        pray.countUp();

        if (pray.getPrayType() == PrayType.SHARED) {
            Pray originPray = prayRepository.getPrayById(pray.getOriginPrayId());
            sendNotificationAndSaveLog(pray, originPray.getMember());
        }
    }

    @Transactional
    public PrayResponseDto deletePray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        prayRepository.delete(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public void prayToGroupPray(PrayToGroupPrayDto prayToGroupPrayDto, String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        Group group = groupRepository.getGroupById(prayToGroupPrayDto.getGroupId());

        List<Pray> mainPray = prayRepository.findAllByIdIn(prayToGroupPrayDto.getPrayId());
        List<Pray> targetPray = prayRepository.findAllByOriginPrayIdIn(
            prayToGroupPrayDto.getPrayId());

        for (Pray p : mainPray) {
            GroupPray groupPray = GroupPray.builder()
                .group(group)
                .author(member)
                .content(p.getContent())
                .deadline(p.getDeadline())
                .build();
            p.setGroupPray(groupPray);
            groupPrayRepository.save(groupPray);

            ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .build();
            scrapAndHeartRepository.save(scrapAndHeart);

            for (Pray TP : targetPray) {
                ScrapAndHeart targetScrapAndHeart = ScrapAndHeart.builder()
                    .groupPray(groupPray)
                    .member(TP.getMember())
                    .build();
                targetScrapAndHeart.scrapPray(TP);
                scrapAndHeartRepository.save(targetScrapAndHeart);
            }
        }
    }
}
