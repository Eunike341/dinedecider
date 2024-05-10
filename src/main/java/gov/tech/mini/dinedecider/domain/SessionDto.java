package gov.tech.mini.dinedecider.domain;
import java.util.List;
import java.util.UUID;

public record SessionDto(UUID sessionUuid, UserDto admin, String sessionName, List<UserDto> invitees, List<UserDto> attendees) {
    public SessionDto (UUID sessionUuid, UserDto admin, String sessionName, List<UserDto> invitees) {
        this(sessionUuid, admin, sessionName, invitees, null);
    }
}
