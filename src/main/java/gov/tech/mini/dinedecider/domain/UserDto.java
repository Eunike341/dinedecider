package gov.tech.mini.dinedecider.domain;

import gov.tech.mini.dinedecider.repo.User;

import java.util.List;
import java.util.UUID;

public record UserDto(UUID userUuid, String name, List<SubmissionDto> submissions) {
    public UserDto (User user) {
        this(user.getUuid(), user.getName(), null);
    }
}
