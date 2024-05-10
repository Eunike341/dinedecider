package gov.tech.mini.dinedecider.controller;

import gov.tech.mini.dinedecider.domain.SessionDto;
import gov.tech.mini.dinedecider.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public void endSession () {

    }

    @PostMapping("/{sessionUuid}/invitations")
    public void inviteUser() {

    }

    @PostMapping("/{sessionUuid}/participants")
    public void joinSession() {

    }


}
