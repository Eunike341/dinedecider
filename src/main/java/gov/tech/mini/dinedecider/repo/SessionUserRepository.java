package gov.tech.mini.dinedecider.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionUserRepository extends JpaRepository<SessionUser, Long> {
    Optional<SessionUser> findByAttendee_UuidAndSession_Uuid(UUID userUuid, UUID sessionUuid);
}
