package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.model.GroupPray;
import com.uspray.uspray.domain.group.repository.GroupPrayRepository;
import java.util.List;
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

    public GroupPray getGroupPrayById(Long id) {
        return groupPrayRepository.getGroupPrayById(id);
    }

    @Transactional
    public void saveGroupPray(GroupPray groupPray) {
        groupPrayRepository.save(groupPray);
    }

    public List<Long> getOriginPrayIdsByGroupId(Long groupId) {
        return groupPrayRepository.getOriginPrayIdByGroupId(groupId);
    }

    public List<GroupPray> findGroupPraysByGroup(Group group) {
        return groupPrayRepository.findGroupPraysByGroup(group);
    }

}
