package gov.tech.mini.dinedecider.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionUserRepository extends JpaRepository<SessionUser, Long> {
    Optional<SessionUser> findByStatusAndAttendee_UuidAndSession_UuidAndSession_Status
            (MemberStatus status, UUID userUuid, UUID sessionUuid, SessionStatus sessionStatus);
}
