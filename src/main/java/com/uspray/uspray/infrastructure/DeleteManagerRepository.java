package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.WithdrawManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteManagerRepository extends JpaRepository<WithdrawManager, Long> {

}
