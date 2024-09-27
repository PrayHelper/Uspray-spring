package com.uspray.uspray.domain.group.repository;

import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.model.GroupPray;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupPrayRepository extends JpaRepository<GroupPray, Long> {

    @EntityGraph(attributePaths = {"author"})
    List<GroupPray> findGroupPraysByGroup(Group group);

    @Query("select g.originPray.id from GroupPray g left join g.originPray left join g.group where g.group.id = :groupId")
    List<Long> getOriginPrayIdByGroupId(@Param("groupId") Long groupId);

    default GroupPray getGroupPrayById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_GROUP_PRAY_EXCEPTION));
    }

    default void deleteAllByGroup(Group group) {
        this.deleteAll(findGroupPraysByGroup(group));
    }
}
