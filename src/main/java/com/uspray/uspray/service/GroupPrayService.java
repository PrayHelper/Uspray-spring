package com.uspray.uspray.service;

import com.uspray.uspray.infrastructure.GroupPrayRepository;
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

}
