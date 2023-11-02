package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.querydsl.pray.PrayRepositoryCustom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrayRepository extends JpaRepository<Pray, Long>, PrayRepositoryCustom {

    default Pray getPrayById(Long id) {
      return findById(id).orElseThrow(
          () -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION,
              ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage()));
    }

    List<Pray> findAllByIdIn(List<Long> prayIds);
}
