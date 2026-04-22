package com.portfolio.integration.dto;

import com.portfolio.integration.domain.InterfaceChannelType;
import com.portfolio.integration.domain.InterfaceDirection;
import com.portfolio.integration.domain.InterfaceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterfaceUpdateRequest(
        @NotBlank String interfaceName,
        @NotBlank String sourceSystem,
        @NotBlank String targetSystem,
        @NotNull InterfaceChannelType channelType,
        @NotNull InterfaceDirection direction,
        @NotNull InterfaceStatus status,
        @NotBlank String ownerTeam,
        @NotBlank String description,
        boolean active
) {
}
