package com.portfolio.integration.dto;

import com.portfolio.integration.domain.ExecutionResultType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterfaceExecutionRequest(
        @NotNull ExecutionResultType resultType,
        @NotBlank String message,
        Boolean retriable
) {
}
