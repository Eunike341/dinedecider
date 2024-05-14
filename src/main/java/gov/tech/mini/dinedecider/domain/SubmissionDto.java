package gov.tech.mini.dinedecider.domain;

import gov.tech.mini.dinedecider.repo.Submission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissionDto(@NotBlank String placeName, @NotNull UserDto submittedBy, boolean selected) {
    public SubmissionDto (Submission submission) {
        this (submission.getPlaceName(), new UserDto(submission.getSessionUser().getAttendee()), submission.isSelected());
    }
}
