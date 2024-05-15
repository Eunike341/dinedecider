package gov.tech.mini.dinedecider.controller;

import gov.tech.mini.dinedecider.domain.*;
import gov.tech.mini.dinedecider.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Session Controller", description = "APIs for managing dining sessions, such as team lunch session.")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(summary = "Start a new session", description = "Starts a new session with the provided details." +
            "If the admin's or invitee's UUID does not exist in the database, a new record will be inserted with the provided UUID and name. " +
            "If the UUID already exists, no insert/ update will be made, even if the name is different."+
            "The UUID is used as an identifier across different microservices as this application does not handle user management directly. " +
            "It is expected to receive requests with new UUIDs and it provides a means to integrate back to the caller." +
            "Invitees is optional. New invites can be sent later with another API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session started successfully", content = @Content(schema = @Schema(implementation = SessionDto.class)))
    })
    @PostMapping
    public ResponseEntity<SessionDto> startSession (@Valid @RequestBody SessionStartRequestDto session) {
        return ResponseEntity.ok(this.sessionService.startSession(session));
    }

    @Operation(summary = "Decide on a place and end the session.", description = "Ends an existing session identified by the session UUID."+
            "A session can only be ended by the same user who started it and it has at least one place recommendation submitted." +
            "The decider implementation is RandomPlaceDecider which picks at a random index of the list of submitted places.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session ended successfully", content = @Content(schema = @Schema(implementation = SubmissionDto.class))),
            @ApiResponse(responseCode = "400", description = "Session has ended or there is no place submitted to be selected", content = @Content),
            @ApiResponse(responseCode = "403", description = "Admin UUID is not the same user who created the session", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session or Admin User not found", content = @Content)
    })
    @PatchMapping("/{sessionUuid}")
    public ResponseEntity<SubmissionDto> endSession (@PathVariable UUID sessionUuid, @Valid @RequestBody AdminUuidDto adminDto) {
        return ResponseEntity.ok(this.sessionService.endSession(sessionUuid, adminDto.adminUuid()));
    }

    @Operation(summary = "Invite users to a session", description = "Sends invitations to a list of users to join the session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Users invited successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
    })
    @PostMapping("/{sessionUuid}/invitations")
    public ResponseEntity<Void> inviteUser(@PathVariable UUID sessionUuid, @NotEmpty @RequestBody List<UserDto> invitees) {
        sessionService.inviteUsers(sessionUuid, invitees);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Join a session", description = "Allows an invited user to accept the invitation to join a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User joined the session successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid attempt to join, could be because the user has joined before, is not invited, does not exist, or the session has ended or does not exist", content = @Content)
    })
    @PatchMapping("/{sessionUuid}/participants/{userUuid}")
    public ResponseEntity<Void> joinSession(@PathVariable UUID sessionUuid, @PathVariable UUID userUuid) {
        sessionService.joinSession(sessionUuid, userUuid);
        return ResponseEntity.noContent().build();
    }

}
