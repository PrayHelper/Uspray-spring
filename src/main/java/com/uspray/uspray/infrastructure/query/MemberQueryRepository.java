package com.uspray.uspray.infrastructure.query;

import com.uspray.uspray.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberQueryRepository extends JpaRepository<Member, Long> {

    @Query("select m.firebaseToken from Member m where m.firstNotiAgree = :agree")
    List<String> getDeviceTokensByFirstNotiAgree(@Param("agree") Boolean agree);

}
