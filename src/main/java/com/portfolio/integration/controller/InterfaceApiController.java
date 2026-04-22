package com.portfolio.integration.controller;

import com.portfolio.integration.domain.InterfaceChannelType;
import com.portfolio.integration.domain.InterfaceStatus;
import com.portfolio.integration.dto.DashboardResponse;
import com.portfolio.integration.dto.ErrorLogResponse;
import com.portfolio.integration.dto.ErrorLogSearchCondition;
import com.portfolio.integration.dto.ExecutionHistoryResponse;
import com.portfolio.integration.dto.InterfaceExecutionRequest;
import com.portfolio.integration.dto.InterfaceRegistrationRequest;
import com.portfolio.integration.dto.InterfaceSearchCondition;
import com.portfolio.integration.dto.InterfaceSummaryResponse;
import com.portfolio.integration.dto.InterfaceStatusUpdateRequest;
import com.portfolio.integration.dto.InterfaceUpdateRequest;
import com.portfolio.integration.service.InterfaceMonitoringService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
@RestController
@RequestMapping("/api")
public class InterfaceApiController {

    private final InterfaceMonitoringService interfaceMonitoringService;

    public InterfaceApiController(InterfaceMonitoringService interfaceMonitoringService) {
        this.interfaceMonitoringService = interfaceMonitoringService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return interfaceMonitoringService.getDashboard();
    }

    @GetMapping("/interfaces")
    public List<InterfaceSummaryResponse> interfaces(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) InterfaceStatus status,
            @RequestParam(required = false) InterfaceChannelType channelType,
            @RequestParam(required = false) Boolean active
    ) {
        return interfaceMonitoringService.search(new InterfaceSearchCondition(keyword, status, channelType, active));
    }

    @GetMapping("/interfaces/{id}")
    public InterfaceSummaryResponse getInterface(@PathVariable Long id) {
        return interfaceMonitoringService.getInterfaceSummary(id);
    }

    @PostMapping("/interfaces")
    @ResponseStatus(HttpStatus.CREATED)
    public InterfaceSummaryResponse register(@Valid @RequestBody InterfaceRegistrationRequest request) {
        return interfaceMonitoringService.register(request);
    }

    @PutMapping("/interfaces/{id}")
    public InterfaceSummaryResponse update(@PathVariable Long id,
                                           @Valid @RequestBody InterfaceUpdateRequest request) {
        return interfaceMonitoringService.update(id, request);
    }

    @PutMapping("/interfaces/{id}/status")
    public InterfaceSummaryResponse updateStatus(@PathVariable Long id,
                                                 @Valid @RequestBody InterfaceStatusUpdateRequest request) {
        return interfaceMonitoringService.changeStatus(id, request);
    }

    @PostMapping("/interfaces/{id}/deactivate")
    public InterfaceSummaryResponse deactivate(@PathVariable Long id) {
        return interfaceMonitoringService.deactivate(id);
    }

    @DeleteMapping("/interfaces/{id}")
    public InterfaceSummaryResponse deactivateByDelete(@PathVariable Long id) {
        return interfaceMonitoringService.deactivate(id);
    }

    @PostMapping("/interfaces/{id}/retry")
    public InterfaceSummaryResponse retry(@PathVariable Long id) {
        return interfaceMonitoringService.retry(id);
    }

    @PostMapping("/interfaces/{id}/execute")
    public InterfaceSummaryResponse recordExecution(@PathVariable Long id,
                                                    @Valid @RequestBody InterfaceExecutionRequest request) {
        return interfaceMonitoringService.recordExecution(id, request);
    }

    @GetMapping("/logs")
    public List<ErrorLogResponse> logs(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Long interfaceId,
                                       @RequestParam(required = false) Boolean retriable) {
        return interfaceMonitoringService.getErrorLogs(new ErrorLogSearchCondition(keyword, interfaceId, retriable));
    }

    @GetMapping("/executions")
    public List<ExecutionHistoryResponse> executions(@RequestParam(required = false) Long interfaceId) {
        if (interfaceId == null) {
            return interfaceMonitoringService.getRecentExecutions(10);
        }
        return interfaceMonitoringService.getExecutionsByInterface(interfaceId);
    }
}
