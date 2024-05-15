package gov.tech.mini.dinedecider.domain;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUuidDto(@NotNull UUID userUuid) {
}
