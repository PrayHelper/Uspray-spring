package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.History;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> getAllByUserIdOrderByDeadlineDesc(String userId);
}
