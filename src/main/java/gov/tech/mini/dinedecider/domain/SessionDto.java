package gov.tech.mini.dinedecider.domain;
import java.util.List;
import java.util.UUID;

public record SessionDto(UserDto admin, String sessionName, List<UserDto> invitees, List<UserDto> attendees) {
    public SessionDto (UserDto admin, String sessionName, List<UserDto> invitees) {
        this(admin, sessionName, invitees, null);
    }
}
