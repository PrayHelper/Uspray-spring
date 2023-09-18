package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.model.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import com.uspray.uspray.exception.ErrorStatus;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Repository
public interface PrayRepository extends JpaRepository<Pray, Long> {
    default Pray getPrayById(Long id) {
        Optional<Pray> optionalPray = findById(id);
        return optionalPray.orElseThrow(() -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION, ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage()));
    }
}
