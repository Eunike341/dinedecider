package gov.tech.mini.dinedecider.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @PostMapping
    public void startSession () {

    }

    @PatchMapping("/{sessionId}")
    public void endSession () {

    }

    @PostMapping("/{sessionId}/invitations")
    public void inviteUser() {

    }

    @PostMapping("/{sessionId}/participants")
    public void joinSession() {

    }


}
