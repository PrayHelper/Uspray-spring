package com.uspray.uspray.service;

import com.uspray.uspray.DTO.grouppray.GroupPrayRequestDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.DTO.grouppray.GroupPrayUpdateDto;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.GroupPrayRepository;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupPrayService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupPrayRepository groupPrayRepository;

    @Transactional
    public void createGroupPray(GroupPrayRequestDto groupPrayRequestDto, String userId) {
        Member author = memberRepository.getMemberByUserId(userId);
        Group group = groupRepository.getGroupById(groupPrayRequestDto.getGroupId());
        GroupPray groupPray = GroupPray.builder()
            .group(group)
            .author(author)
            .content(groupPrayRequestDto.getContent())
            .build();
        groupPrayRepository.save(groupPray);
    }

    @Transactional
    public void updateGroupPray(GroupPrayUpdateDto groupPrayUpdateDto) {
        GroupPray groupPray = groupPrayRepository.getGroupPrayById(
            groupPrayUpdateDto.getGroupPrayId());
        groupPray.changeContent(groupPrayUpdateDto.getContent());
    }

    //groupId와 자신의 Id를 이용해 group pray들 반환 + 작성자인지 and 좋아요를 눌렀는지 확인 가능
    @Transactional(readOnly = true)
    public List<GroupPrayResponseDto> getGroupPray(Long groupId, String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        Group group = groupRepository.getGroupById(groupId);

        List<GroupPrayResponseDto> groupPrayList = groupPrayRepository.getGroupPrayList(group, member);

        for (GroupPrayResponseDto groupPrayResponseDto : groupPrayList) {
            if (groupPrayResponseDto.getAuthorId().equals(member.getId())) {
                groupPrayResponseDto.changeOwner();
            }
        }

        return groupPrayList;
    }

    @Transactional
    public void deleteGroupPray(Long groupPrayId) {
        groupPrayRepository.delete(groupPrayRepository.getGroupPrayById(groupPrayId));
    }

}
