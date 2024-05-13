package gov.tech.mini.dinedecider.controller;

import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/{sessionUuid}")
    public ResponseEntity<Void> submitPlace (@PathVariable UUID sessionUuid, @RequestBody SubmissionDto submissionDto) {
        submissionService.submitPlace(sessionUuid, submissionDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionUuid}")
    public ResponseEntity<List<SubmissionDto>> viewSubmission (@PathVariable UUID sessionUuid) {
        return ResponseEntity.ok(submissionService.getSubmittedPlaces(sessionUuid));
    }

    @GetMapping("/{sessionUuid}/selected")
    public ResponseEntity<SubmissionDto> viewSelectedPlace (@PathVariable UUID sessionUuid) {
        return ResponseEntity.ok(submissionService.getSelectedPlace(sessionUuid));
    }

}
