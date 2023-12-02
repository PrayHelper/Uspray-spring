package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.NotificationLog;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.HistoryRepository;
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
public class PrayFacade {

    private final MemberRepository memberRepository;
    private final PrayRepository prayRepository;
    private final CategoryRepository categoryRepository;
    private final HistoryRepository historyRepository;
    private final NotificationLogRepository notificationLogRepository;

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
        if (pray.getPrayType() == PrayType.GROUP) {
            throw new CustomException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
                ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
        }
        // 이 기도 제목을 공유한 적 없거나, 공유 받은 사람이 없으면 전부 수정 가능
        // 이 기도 제목을 공유한 적 있고, 누구라도 공유 받은 사람이 있으면 기도제목 내용 수정 불가능
        Pray sharedPray = prayRepository.getPrayByOriginPrayId(prayId);
        if (prayUpdateRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.getCategoryByIdAndMember(
                prayUpdateRequestDto.getCategoryId(),
                pray.getMember());
            pray.update(prayUpdateRequestDto,
                sharedPray != null || pray.getPrayType() == PrayType.SHARED, category);
        }

        pray.update(prayUpdateRequestDto,
            sharedPray != null || pray.getPrayType() == PrayType.SHARED, null);

        return PrayResponseDto.of(pray);
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

    private void sendNotificationAndSaveLog(Pray pray, String username) {
        // TODO: notification 보내는 로직 추가

        System.out.println("send notification to " + memberRepository.getMemberByUserId(username));
        NotificationLog notificationLog = NotificationLog.builder()
            .pray(pray)
            .member(memberRepository.getMemberByUserId(username))
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
            sendNotificationAndSaveLog(pray, originPray.getMember().getUserId());
        }
    }
}
