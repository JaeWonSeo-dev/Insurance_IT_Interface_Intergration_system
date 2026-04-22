package com.portfolio.integration.dto;

import com.portfolio.integration.domain.InterfaceChannelType;
import com.portfolio.integration.domain.InterfaceDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterfaceRegistrationRequest(
        @NotBlank String interfaceCode,
        @NotBlank String interfaceName,
        @NotBlank String sourceSystem,
        @NotBlank String targetSystem,
        @NotNull InterfaceChannelType channelType,
        @NotNull InterfaceDirection direction,
        @NotBlank String ownerTeam,
        @NotBlank String description
) {
}
