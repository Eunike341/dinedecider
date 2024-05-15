package gov.tech.mini.dinedecider.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SessionStartRequestDto(@NotNull UserDto admin, @NotBlank String sessionName, List<UserDto> invitees) {
}
