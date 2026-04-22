package com.portfolio.integration.dto;

import com.portfolio.integration.domain.ExecutionResultType;
import com.portfolio.integration.domain.InterfaceStatus;

import java.time.LocalDateTime;

public record ExecutionHistoryResponse(
        Long id,
        Long interfaceId,
        String interfaceCode,
        InterfaceStatus status,
        ExecutionResultType resultType,
        String message,
        LocalDateTime executedAt,
        boolean retried
) {
}
