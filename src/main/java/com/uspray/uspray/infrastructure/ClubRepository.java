package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Club;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {

    default Club getClubById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_CLUB_EXCEPTION,
                ErrorStatus.NOT_FOUND_CLUB_EXCEPTION.getMessage()));
    }

}
