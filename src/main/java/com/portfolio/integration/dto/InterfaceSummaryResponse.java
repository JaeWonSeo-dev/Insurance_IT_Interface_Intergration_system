package com.portfolio.integration.dto;

import com.portfolio.integration.domain.InterfaceChannelType;
import com.portfolio.integration.domain.InterfaceDirection;
import com.portfolio.integration.domain.InterfaceStatus;

import java.time.LocalDateTime;

public record InterfaceSummaryResponse(
        Long id,
        String interfaceCode,
        String interfaceName,
        String sourceSystem,
        String targetSystem,
        InterfaceChannelType channelType,
        InterfaceDirection direction,
        InterfaceStatus status,
        int successCount,
        int failureCount,
        LocalDateTime lastExecutionAt,
        String ownerTeam,
        String description,
        boolean active
) {
}
