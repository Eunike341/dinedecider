package gov.tech.mini.dinedecider.controller;

import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.SubmissionRequestDto;
import gov.tech.mini.dinedecider.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
@Validated
@Tag(name = "Submission Controller", description = "APIs for handling and viewing submissions of places for a dining session such as a team lunch")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Operation(summary = "Submit a place", description = "Nominate a place for the team to go for lunch." +
                "The name of the place is free text to accommodate less formal place that is known within the team." +
                "Users are able to submit multiple times, and the same place can be submitted again.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Place submitted successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid submission could happen if the session has ended, or user has not joined, or not exist", content = @Content)
    })
    @PostMapping("/{sessionUuid}")
    public ResponseEntity<Void> submitPlace (@PathVariable UUID sessionUuid,
                                             @Valid @RequestBody SubmissionRequestDto submissionDto) {
        submissionService.submitPlace(sessionUuid, submissionDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "View submissions", description = "Returns a list of submitted places for a given session UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of submitted places retrieved successfully, or empty list when there is no submitted place yet.", content = @Content(schema = @Schema(implementation = SubmissionDto.class))),
            @ApiResponse(responseCode = "403", description = "Only users who are in the session are able to see restaurants that others have submitted", content = @Content)
    })
    @GetMapping("/{sessionUuid}")
    public ResponseEntity<List<SubmissionDto>> viewSubmission (@PathVariable UUID sessionUuid, @RequestParam UUID userUuid) {
        return ResponseEntity.ok(submissionService.getSubmittedPlaces(sessionUuid, userUuid));
    }

    @Operation(summary = "View selected place", description = "Returns the selected place for a given session UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Selected place retrieved successfully, or empty response when there is no selected place yet.", content = @Content(schema = @Schema(implementation = SubmissionDto.class))),
            @ApiResponse(responseCode = "403", description = "Only users who are in the session are able to see able to see the picked restaurant.", content = @Content)
    })
    @GetMapping("/{sessionUuid}/selected")
    public ResponseEntity<SubmissionDto> viewSelectedPlace (@PathVariable UUID sessionUuid,  @RequestParam UUID userUuid) {
        return ResponseEntity.ok(submissionService.getSelectedPlace(sessionUuid, userUuid));
    }

}
