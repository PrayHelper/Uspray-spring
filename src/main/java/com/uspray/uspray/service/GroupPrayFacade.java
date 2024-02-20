package com.uspray.uspray.service;

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
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.ScrapAndHeart;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.GroupMemberRepository;
import com.uspray.uspray.infrastructure.GroupPrayRepository;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import com.uspray.uspray.infrastructure.ScrapAndHeartRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void prayToGroupPray(PrayToGroupPrayDto prayToGroupPrayDto, String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        Group group = groupRepository.getGroupById(prayToGroupPrayDto.getGroupId());

        List<Long> existIds = group.getGroupPrayList().stream()
            .map(gp -> gp.getOriginPray().getId())
            .collect(Collectors.toList());
        if (existIds.stream().anyMatch(id -> id.equals(prayToGroupPrayDto.getPrayId()))) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_PRAY_EXCEPTION,
                ErrorStatus.ALREADY_EXIST_GROUP_PRAY_EXCEPTION.getMessage());
        }

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
                    .member(groupPray.getOriginPray().getMember())
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
            return;
        }
        scrapAndHeartByGroupPrayAndMember.get().heartPray();
    }

    @Transactional
    public void scrapGroupPray(ScrapRequestDto scrapRequestDto, String userId) {
        GroupPray groupPray = groupPrayRepository.getGroupPrayById(
            scrapRequestDto.getGroupPrayId());
        Member member = memberRepository.getMemberByUserId(userId);

        Optional<ScrapAndHeart> scrapAndHeartByGroupPrayAndMember = scrapAndHeartRepository.findScrapAndHeartByGroupPrayAndMember(
            groupPray, member);

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
            return;
        }
        Pray pray = makePray(scrapRequestDto, groupPray, member);
        prayRepository.save(pray);
        scrapAndHeartByGroupPrayAndMember.get().scrapPray(pray);
    }

    private Pray makePray(ScrapRequestDto scrapRequestDto, GroupPray groupPray, Member member) {
        Category category = categoryRepository.getCategoryByIdAndMember(
            scrapRequestDto.getCategoryId(),
            member);

        return Pray.builder()
            .content(groupPray.getContent())
            .deadline(scrapRequestDto.getDeadline())
            .member(member)
            .category(category)
            .prayType(PrayType.SHARED)
            .build();
    }
}
