package com.portfolio.integration.dto;

public record ErrorLogSearchCondition(
        String keyword,
        Long interfaceId,
        Boolean retriable
) {
}
