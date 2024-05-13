package gov.tech.mini.dinedecider.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByUuid(UUID uuid);
    Optional<Session> findByUuidAndStatus(UUID uuid, SessionStatus sessionStatus);
}
