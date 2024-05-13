package gov.tech.mini.dinedecider.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<List<Submission>> findBySessionUser_Session_Uuid(final UUID sessionUuid);
    Optional<Submission> findBySelectedAndSessionUser_Session_Uuid(final boolean selected, final UUID sessionUuid);
}
