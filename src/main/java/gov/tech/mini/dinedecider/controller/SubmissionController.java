package gov.tech.mini.dinedecider.controller;

import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.validation.ValidUUID;
import gov.tech.mini.dinedecider.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
@Validated
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/{sessionUuid}")
    public ResponseEntity<Void> submitPlace (@PathVariable @ValidUUID String sessionUuid,
                                             @Valid @RequestBody SubmissionDto submissionDto) {
        submissionService.submitPlace(UUID.fromString(sessionUuid), submissionDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionUuid}")
    public ResponseEntity<List<SubmissionDto>> viewSubmission (@PathVariable @ValidUUID String sessionUuid) {
        return ResponseEntity.ok(submissionService.getSubmittedPlaces(UUID.fromString(sessionUuid)));
    }

    @GetMapping("/{sessionUuid}/selected")
    public ResponseEntity<SubmissionDto> viewSelectedPlace (@PathVariable @ValidUUID String sessionUuid) {
        return ResponseEntity.ok(submissionService.getSelectedPlace(UUID.fromString(sessionUuid)));
    }

}
