package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrayRepository extends JpaRepository<Pray, Long> {

  @Query("SELECT p FROM Pray p JOIN p.member m WHERE m.userId = :userId")
  List<Pray> findAllByUserId(@Param("userId") String userId);

  default Pray getPrayById(Long id) {
    return findById(id).orElseThrow(
        () -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION,
            ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage()));
  }
}
