package com.portfolio.integration.dto;

public record DashboardMetrics(
        int totalInterfaces,
        long runningCount,
        long warningCount,
        long failedCount,
        long pausedCount,
        int totalSuccessCount,
        int totalFailureCount,
        double successRate
) {
}
