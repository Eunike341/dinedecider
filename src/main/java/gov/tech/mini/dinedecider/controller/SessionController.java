package gov.tech.mini.dinedecider.controller;

import gov.tech.mini.dinedecider.domain.SessionDto;
import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.UserDto;
import gov.tech.mini.dinedecider.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionDto> startSession (@RequestBody SessionDto session) {
        return ResponseEntity.ok(this.sessionService.startSession(session));
    }

    @PatchMapping("/{sessionUuid}")
    public ResponseEntity<SubmissionDto> endSession (@PathVariable UUID sessionUuid, @RequestBody UserDto adminDto) {
        return ResponseEntity.ok(this.sessionService.endSession(sessionUuid, adminDto.userUuid()));
    }

    @PostMapping("/{sessionUuid}/invitations")
    public ResponseEntity<Void> inviteUser(@PathVariable UUID sessionUuid, @RequestBody List<UserDto> invitees) {
        sessionService.inviteUsers(sessionUuid, invitees);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{sessionUuid}/participants/{userUuid}")
    public ResponseEntity<Void> joinSession(@PathVariable UUID sessionUuid, @PathVariable UUID userUuid) {
        sessionService.joinSession(sessionUuid, userUuid);
        return ResponseEntity.noContent().build();
    }

}
