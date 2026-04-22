package com.portfolio.integration.dto;

import com.portfolio.integration.domain.InterfaceStatus;
import jakarta.validation.constraints.NotNull;

public record InterfaceStatusUpdateRequest(
        @NotNull InterfaceStatus status
) {
}
