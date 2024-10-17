package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.repository.GroupPrayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupPrayService {

    private final GroupPrayRepository groupPrayRepository;


    @Transactional
    public void deleteGroupPray(Long groupPrayId) {
        groupPrayRepository.delete(groupPrayRepository.getGroupPrayById(groupPrayId));
    }

    @Transactional
    public void deleteAllByGroup(Group group) {
        groupPrayRepository.deleteAllByGroup(group);
    }

}
