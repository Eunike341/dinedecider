package gov.tech.mini.dinedecider.domain;

import gov.tech.mini.dinedecider.repo.Submission;

public record SubmissionDto(String placeName, UserDto submittedBy, boolean selected) {
    public SubmissionDto (Submission submission) {
        this (submission.getPlaceName(), new UserDto(submission.getSessionUser().getAttendee()), submission.isSelected());
    }
}
