package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPrayRepository extends JpaRepository<GroupPray, Long>{

    @EntityGraph(attributePaths = {"author"})
    List<GroupPray> findGroupPraysByGroup(Group group);

    default GroupPray getGroupPrayById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_GROUP_PRAY_EXCEPTION,
                ErrorStatus.NOT_FOUND_GROUP_PRAY_EXCEPTION.getMessage()));
    }

}
