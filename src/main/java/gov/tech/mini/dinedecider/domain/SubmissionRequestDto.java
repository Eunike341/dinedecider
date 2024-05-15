package gov.tech.mini.dinedecider.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissionRequestDto(@NotBlank String placeName, @NotNull UserUuidDto submittedBy) {
}
