package com.portfolio.integration.dto;

import java.util.List;

public record DashboardResponse(
        DashboardMetrics metrics,
        List<ErrorLogResponse> recentErrorLogs,
        List<ExecutionHistoryResponse> recentExecutions
) {
}
