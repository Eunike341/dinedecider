package gov.tech.mini.dinedecider.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionAttendeeRepository extends JpaRepository<SessionInvitee, Long> {
}
