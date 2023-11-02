package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.ClubPray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubPrayRepository extends JpaRepository<ClubPray, Long> {

    default ClubPray getClubPrayById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_CLUB_PRAY_EXCEPTION,
                ErrorStatus.NOT_FOUND_CLUB_PRAY_EXCEPTION.getMessage()));
    }

}
