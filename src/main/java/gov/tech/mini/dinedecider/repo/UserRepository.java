package gov.tech.mini.dinedecider.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByNameIn(List<String> names);

    Optional<User> findByUuid(UUID userUuid);

    List<User> findByUuidIn(List<UUID> uuids);
}
