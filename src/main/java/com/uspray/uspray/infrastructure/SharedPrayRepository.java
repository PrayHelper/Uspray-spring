package com.uspray.uspray.infrastructure;

import com.uspray.uspray.DTO.pray.PrayDto;
import com.uspray.uspray.domain.SharedPray;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedPrayRepository extends JpaRepository<SharedPray, Long> {

    List<SharedPray> findAllByUserId(String username);
}
