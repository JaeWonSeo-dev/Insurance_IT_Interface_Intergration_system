package com.portfolio.integration.dto;

import java.time.LocalDateTime;

public record ErrorLogResponse(
        Long id,
        Long interfaceId,
        String interfaceCode,
        String message,
        String detail,
        LocalDateTime occurredAt,
        boolean retriable
) {
}
