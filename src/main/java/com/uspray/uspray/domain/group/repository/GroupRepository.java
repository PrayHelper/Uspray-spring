package com.uspray.uspray.domain.group.repository;

import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.domain.group.repository.querydsl.GroupRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long>, GroupRepositoryCustom {

    Boolean existsByName(String name);

    default Group getGroupById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_GROUP_EXCEPTION));
    }
}
