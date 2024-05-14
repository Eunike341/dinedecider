package gov.tech.mini.dinedecider.controller;

import gov.tech.mini.dinedecider.domain.SessionDto;
import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.UserDto;
import gov.tech.mini.dinedecider.domain.validation.ValidUUID;
import gov.tech.mini.dinedecider.service.SessionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
@Validated
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionDto> startSession (@Valid @RequestBody SessionDto session) {
        return ResponseEntity.ok(this.sessionService.startSession(session));
    }

    @PatchMapping("/{sessionUuid}")
    public ResponseEntity<SubmissionDto> endSession (@PathVariable @ValidUUID String sessionUuid, @Valid @RequestBody UserDto adminDto) {
        return ResponseEntity.ok(this.sessionService.endSession(UUID.fromString(sessionUuid), adminDto.userUuid()));
    }

    @PostMapping("/{sessionUuid}/invitations")
    public ResponseEntity<Void> inviteUser(@PathVariable @ValidUUID String sessionUuid, @NotEmpty @RequestBody List<UserDto> invitees) {
        sessionService.inviteUsers(UUID.fromString(sessionUuid), invitees);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{sessionUuid}/participants/{userUuid}")
    public ResponseEntity<Void> joinSession(@PathVariable @ValidUUID String sessionUuid, @PathVariable @ValidUUID String userUuid) {
        sessionService.joinSession(UUID.fromString(sessionUuid), UUID.fromString(userUuid));
        return ResponseEntity.noContent().build();
    }

}
