package com.uspray.uspray.service.facade;

import com.uspray.uspray.DTO.grouppray.GroupPrayRappingDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayRequestDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.DTO.grouppray.ScrapRequestDto;
import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayToGroupPrayDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.GroupMember;
import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.NotificationLog;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.ScrapAndHeart;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.GroupMemberRepository;
import com.uspray.uspray.infrastructure.GroupPrayRepository;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.NotificationLogRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import com.uspray.uspray.infrastructure.ScrapAndHeartRepository;
import com.uspray.uspray.service.FCMNotificationService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupPrayFacade {

    private final GroupPrayRepository groupPrayRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final ScrapAndHeartRepository scrapAndHeartRepository;
    private final CategoryRepository categoryRepository;
    private final PrayRepository prayRepository;
    private final GroupRepository groupRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final FCMNotificationService fcmNotificationService;

    @Transactional
    public void prayToGroupPray(PrayToGroupPrayDto prayToGroupPrayDto, String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        Group group = groupRepository.getGroupById(prayToGroupPrayDto.getGroupId());

        List<Long> existIds = group.getGroupPrayList().stream()
            .map(gp -> gp.getOriginPray().getId())
            .collect(Collectors.toList());
        if (existIds.stream().anyMatch(id -> id.equals(prayToGroupPrayDto.getPrayId()))) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_PRAY_EXCEPTION);
        }

        List<Pray> mainPray = prayRepository.findAllByIdIn(prayToGroupPrayDto.getPrayId());
        List<Pray> targetPray = prayRepository.findAllByOriginPrayIdIn(
            prayToGroupPrayDto.getPrayId());

        for (Pray p : mainPray) {
            GroupPray groupPray = GroupPray.builder()
                .group(group)
                .author(member)
                .content(new String(Base64.getDecoder().decode(p.getContent().getBytes())))
                .deadline(p.getDeadline())
                .build();
            p.setGroupPray(groupPray);
            groupPrayRepository.save(groupPray);
            p.setIsShared();

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

    public List<PrayListResponseDto> getPrayList(String username, String prayType, Long groupId) {
        List<Long> prayIds = groupPrayRepository.getOriginPrayIdByGroupId(groupId);
        return categoryRepository.findAllWithOrderAndType(username, prayType, prayIds);
    }


    @Transactional
    public void createGroupPray(GroupPrayRequestDto groupPrayRequestDto, String userId) {
        Member author = memberRepository.getMemberByUserId(userId);
        Group group = groupRepository.getGroupById(groupPrayRequestDto.getGroupId());
        Category category = categoryRepository.getCategoryByIdAndMember(
            groupPrayRequestDto.getCategoryId(),
            author);

        GroupPray groupPray = GroupPray.builder()
            .group(group)
            .author(author)
            .deadline(groupPrayRequestDto.getDeadline())
            .content(groupPrayRequestDto.getContent())
            .build();
        groupPrayRepository.save(groupPray);

        ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
            .groupPray(groupPray)
            .member(author)
            .build();
        scrapAndHeartRepository.save(scrapAndHeart);

        Pray pray = Pray.builder()
            .content(groupPrayRequestDto.getContent())
            .deadline(groupPrayRequestDto.getDeadline())
            .member(author)
            .category(category)
            .startDate(LocalDate.now())
            .prayType(PrayType.PERSONAL)
            .groupPray(groupPray)
            .build();
        pray.setIsShared();
        prayRepository.save(pray);
    }

    @Transactional(readOnly = true)
    public GroupPrayRappingDto getGroupPray(Long groupId, String userId) {

        Member member = memberRepository.getMemberByUserId(userId);
        Group group = groupRepository.getGroupById(groupId);
        GroupMember groupMember = groupMemberRepository.getGroupMemberByGroupAndMember(group,
            member);
        List<GroupPray> groupPrays = groupPrayRepository.findGroupPraysByGroup(group);

        Long count = scrapAndHeartRepository.countHeart(groupPrays, true);

        List<GroupPrayResponseDto> groupPrayList = new ArrayList<>();

        for (GroupPray groupPray : groupPrays) {
            Optional<ScrapAndHeart> SH = scrapAndHeartRepository.findScrapAndHeartByGroupPrayAndMember(
                groupPray, member);
            if (SH.isPresent()) {
                ScrapAndHeart scrapAndHeart = SH.get();
                groupPrayList.add(GroupPrayResponseDto.builder()
                    .groupPray(groupPray)
                    .member(member)
                    .scrapAndHeart(scrapAndHeart)
                    .build());
                continue;
            }
            groupPrayList.add(GroupPrayResponseDto.builder()
                .groupPray(groupPray)
                .member(member)
                .build());
        }

        Map<LocalDate, List<GroupPrayResponseDto>> map = groupPrayList.stream()
            .collect(Collectors.groupingBy(GroupPrayResponseDto::getCreatedAt));

        for (Map.Entry<LocalDate, List<GroupPrayResponseDto>> entry : map.entrySet()) {
            List<GroupPrayResponseDto> value = entry.getValue();
            value.sort(Comparator.comparing(GroupPrayResponseDto::getGroupPrayId).reversed());
        }

        return new GroupPrayRappingDto(count, groupMember.getNotificationAgree(), map);
    }

    @Transactional
    public void heartGroupPray(Long groupPrayId, String userId) {
        GroupPray groupPray = groupPrayRepository.getGroupPrayById(groupPrayId);
        Member member = memberRepository.getMemberByUserId(userId);

        Optional<ScrapAndHeart> scrapAndHeartByGroupPrayAndMember = scrapAndHeartRepository.findScrapAndHeartByGroupPrayAndMember(
            groupPray, member);

        if (scrapAndHeartByGroupPrayAndMember.isEmpty()) {
            ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .build();
            scrapAndHeart.heartPray();
            scrapAndHeartRepository.save(scrapAndHeart);
            sendNotificationAndSaveLog(scrapAndHeart, groupPray, groupPray.getAuthor(), true);
            return;
        }
        scrapAndHeartByGroupPrayAndMember.get().heartPray();
        sendNotificationAndSaveLog(scrapAndHeartByGroupPrayAndMember.get(), groupPray, groupPray.getAuthor(), true);
    }

    @Transactional
    public void scrapGroupPray(ScrapRequestDto scrapRequestDto, String userId) {
        Category category = categoryRepository.getCategoryById(scrapRequestDto.getCategoryId());
        if (!category.getCategoryType().toString().equals(PrayType.SHARED.toString())) {
            throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH);
        }
        GroupPray groupPray = groupPrayRepository.getGroupPrayById(
            scrapRequestDto.getGroupPrayId());
        Member member = memberRepository.getMemberByUserId(userId);

        Optional<ScrapAndHeart> scrapAndHeartByGroupPrayAndMember = scrapAndHeartRepository.findScrapAndHeartByGroupPrayAndMember(
            groupPray, member);
        Pray originPray = prayRepository.getPrayById(groupPray.getOriginPray().getId());
        originPray.setIsShared();

        if (scrapAndHeartByGroupPrayAndMember.isEmpty()) {
            Pray pray = makePray(scrapRequestDto, groupPray, member);

            ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .sharedPray(pray)
                .build();
            prayRepository.save(pray);
            scrapAndHeart.scrapPray(pray);
            scrapAndHeartRepository.save(scrapAndHeart);
            sendNotificationAndSaveLog(scrapAndHeart, groupPray, groupPray.getAuthor(), false);
            return;
        }
        Pray pray = makePray(scrapRequestDto, groupPray, member);
        prayRepository.save(pray);
        scrapAndHeartByGroupPrayAndMember.get().scrapPray(pray);
        sendNotificationAndSaveLog(scrapAndHeartByGroupPrayAndMember.get(), groupPray, groupPray.getAuthor(), false);
    }

    private Pray makePray(ScrapRequestDto scrapRequestDto, GroupPray groupPray, Member member) {
        Category category = categoryRepository.getCategoryByIdAndMember(
            scrapRequestDto.getCategoryId(),
            member);

        return Pray.builder()
            .content(groupPray.getContent())
            .deadline(scrapRequestDto.getDeadline())
            .member(member)
            .originMemberId(groupPray.getAuthor().getId())
            .originPrayId(groupPray.getOriginPray().getId())
            .category(category)
            .prayType(PrayType.SHARED)
            .build();
    }

    private void sendNotificationAndSaveLog(ScrapAndHeart scrapAndHeart, GroupPray groupPray, Member receiver, boolean isHeart) {
        String groupName = groupPray.getGroup().getName();
        String name = scrapAndHeart.getMember().getName();
        if (isHeart) {
            try {
                fcmNotificationService.sendMessageTo(
                    receiver.getFirebaseToken(),
                    groupName + " 💘",
                    name + "님이 당신의 기도제목을 두고 기도했어요");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            log.error(
                "send notification to " + memberRepository.getMemberByUserId(receiver.getUserId())
            );
            NotificationLog notificationLog = NotificationLog.builder()
                .pray(groupPray.getOriginPray())
                .member(memberRepository.getMemberByUserId(receiver.getUserId()))
                .title(name + "님이 당신의 기도제목을 두고 기도했어요")
                .build();
            notificationLogRepository.save(notificationLog);
            return;
        }
        try {
            fcmNotificationService.sendMessageTo(
                receiver.getFirebaseToken(),
                groupName + " 💌 ",
                name + "님이 당신의 기도제목을 저장했어요");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.error(
            "send notification to " + memberRepository.getMemberByUserId(receiver.getUserId())
        );
        NotificationLog notificationLog = NotificationLog.builder()
            .pray(groupPray.getOriginPray())
            .member(memberRepository.getMemberByUserId(receiver.getUserId()))
            .title(name + "님이 당신의 기도제목을 저장했어요")
            .build();
        notificationLogRepository.save(notificationLog);

    }
}
