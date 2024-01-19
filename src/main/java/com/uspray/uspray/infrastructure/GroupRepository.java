package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Group;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.querydsl.group.GroupRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long>, GroupRepositoryCustom {

    Boolean existsByName(String name);

    default Group getGroupById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_GROUP_EXCEPTION,
                ErrorStatus.NOT_FOUND_GROUP_EXCEPTION.getMessage()));
    }
}
