package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByPhone(String phone);
    
    Optional<Member> findBySocialId(String socialId);

    Member findByNameAndPhone(String name, String phone);

    Member findByUserIdAndPhone(String userId, String phone);

    default Member getMemberByUserId(String userId) {
        return this.findByUserId(userId).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_USER_EXCEPTION,
                ErrorStatus.NOT_FOUND_USER_EXCEPTION.getMessage()));
    }

    default Member getMemberById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_USER_EXCEPTION,
                ErrorStatus.NOT_FOUND_USER_EXCEPTION.getMessage()));
    }
}
