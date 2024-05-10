package gov.tech.mini.dinedecider.service;

import gov.tech.mini.dinedecider.repo.User;
import gov.tech.mini.dinedecider.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrInsert(UUID userUuid, String userName) {
        return userRepository.findByUuid(userUuid)
                .orElse(userRepository.save(new User(userUuid, userName, LocalDateTime.now())));
    }
}
