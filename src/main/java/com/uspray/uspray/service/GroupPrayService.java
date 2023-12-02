package com.uspray.uspray.service;

import com.uspray.uspray.DTO.grouppray.GroupPrayUpdateDto;
import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.infrastructure.GroupPrayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupPrayService {

    private final GroupPrayRepository groupPrayRepository;



    @Transactional
    public void updateGroupPray(GroupPrayUpdateDto groupPrayUpdateDto) {
        GroupPray groupPray = groupPrayRepository.getGroupPrayById(
            groupPrayUpdateDto.getGroupPrayId());
        groupPray.changeContent(groupPrayUpdateDto.getContent());
    }

    @Transactional
    public void deleteGroupPray(Long groupPrayId) {
        groupPrayRepository.delete(groupPrayRepository.getGroupPrayById(groupPrayId));
    }

}
