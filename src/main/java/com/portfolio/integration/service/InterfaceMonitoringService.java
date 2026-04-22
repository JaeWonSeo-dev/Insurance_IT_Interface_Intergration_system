package com.portfolio.integration.service;

import com.portfolio.integration.domain.ExecutionResultType;
import com.portfolio.integration.domain.ErrorLog;
import com.portfolio.integration.domain.ErrorLogRepository;
import com.portfolio.integration.domain.InsuranceInterface;
import com.portfolio.integration.domain.InsuranceInterfaceRepository;
import com.portfolio.integration.domain.InterfaceExecutionHistory;
import com.portfolio.integration.domain.InterfaceExecutionHistoryRepository;
import com.portfolio.integration.domain.InterfaceStatus;
import com.portfolio.integration.dto.DashboardMetrics;
import com.portfolio.integration.dto.DashboardResponse;
import com.portfolio.integration.dto.ErrorLogResponse;
import com.portfolio.integration.dto.ErrorLogSearchCondition;
import com.portfolio.integration.dto.ExecutionHistoryResponse;
import com.portfolio.integration.dto.InterfaceRegistrationRequest;
import com.portfolio.integration.dto.InterfaceSearchCondition;
import com.portfolio.integration.dto.InterfaceStatusUpdateRequest;
import com.portfolio.integration.dto.InterfaceSummaryResponse;
import com.portfolio.integration.dto.InterfaceUpdateRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InterfaceMonitoringService {

        private final InsuranceInterfaceRepository interfaceRepository;
        private final ErrorLogRepository errorLogRepository;
        private final InterfaceExecutionHistoryRepository executionHistoryRepository;

        public InterfaceMonitoringService(InsuranceInterfaceRepository interfaceRepository,
                                                                          ErrorLogRepository errorLogRepository,
                                                                          InterfaceExecutionHistoryRepository executionHistoryRepository) {
                this.interfaceRepository = interfaceRepository;
                this.errorLogRepository = errorLogRepository;
                this.executionHistoryRepository = executionHistoryRepository;
    }

        public DashboardResponse getDashboard() {
                return new DashboardResponse(
                                getDashboardMetrics(),
                                getErrorLogs(new ErrorLogSearchCondition(null, null, null)).stream().limit(5).toList(),
                                getRecentExecutions(10)
                );
        }

    public List<InsuranceInterface> getInterfaces() {
                return interfaceRepository.findAll().stream()
                                .sorted(Comparator.comparing(InsuranceInterface::getLastExecutionAt).reversed())
                .toList();
    }

    public List<InterfaceSummaryResponse> search(InterfaceSearchCondition condition) {
        String keyword = condition.keyword() == null ? "" : condition.keyword().trim().toLowerCase();

                return interfaceRepository.findAll((root, query, cb) -> {
                                        Predicate predicate = cb.conjunction();
                                        if (!keyword.isBlank()) {
                                                String pattern = "%" + keyword + "%";
                                                predicate = cb.and(predicate, cb.or(
                                                                cb.like(cb.lower(root.get("interfaceCode")), pattern),
                                                                cb.like(cb.lower(root.get("interfaceName")), pattern),
                                                                cb.like(cb.lower(root.get("sourceSystem")), pattern),
                                                                cb.like(cb.lower(root.get("targetSystem")), pattern),
                                                                cb.like(cb.lower(root.get("ownerTeam")), pattern)
                                                ));
                                        }
                                        if (condition.status() != null) {
                                                predicate = cb.and(predicate, cb.equal(root.get("status"), condition.status()));
                                        }
                                        if (condition.channelType() != null) {
                                                predicate = cb.and(predicate, cb.equal(root.get("channelType"), condition.channelType()));
                                        }
                                        if (condition.active() != null) {
                                                predicate = cb.and(predicate, cb.equal(root.get("active"), condition.active()));
                                        }
                                        return predicate;
                                }).stream()
                                .sorted(Comparator.comparing(InsuranceInterface::getLastExecutionAt).reversed())
                .map(this::toSummary)
                .toList();
    }

    public InsuranceInterface getInterface(Long id) {
                return interfaceRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("대상 인터페이스를 찾을 수 없습니다. id=" + id));
    }

        public InterfaceSummaryResponse getInterfaceSummary(Long id) {
                return toSummary(getInterface(id));
        }

        public List<ErrorLogResponse> getErrorLogs(ErrorLogSearchCondition condition) {
                String keyword = condition.keyword() == null ? "" : condition.keyword().trim().toLowerCase();

                return errorLogRepository.findAll((root, query, cb) -> {
                                        Predicate predicate = cb.conjunction();
                                        if (!keyword.isBlank()) {
                                                String pattern = "%" + keyword + "%";
                                                predicate = cb.and(predicate, cb.or(
                                                                cb.like(cb.lower(root.get("interfaceCode")), pattern),
                                                                cb.like(cb.lower(root.get("message")), pattern),
                                                                cb.like(cb.lower(root.get("detail")), pattern)
                                                ));
                                        }
                                        if (condition.interfaceId() != null) {
                                                predicate = cb.and(predicate, cb.equal(root.get("interfaceId"), condition.interfaceId()));
                                        }
                                        if (condition.retriable() != null) {
                                                predicate = cb.and(predicate, cb.equal(root.get("retriable"), condition.retriable()));
                                        }
                                        return predicate;
                                }).stream()
                                .sorted(Comparator.comparing(ErrorLog::getOccurredAt).reversed())
                                .map(this::toLogResponse)
                .toList();
    }

    public DashboardMetrics getDashboardMetrics() {
                List<InsuranceInterface> interfaces = interfaceRepository.findAll();
                int totalSuccess = interfaces.stream().mapToInt(InsuranceInterface::getSuccessCount).sum();
                int totalFailure = interfaces.stream().mapToInt(InsuranceInterface::getFailureCount).sum();
        double successRate = (totalSuccess + totalFailure) == 0
                ? 0.0
                : ((double) totalSuccess / (totalSuccess + totalFailure)) * 100;

        return new DashboardMetrics(
                interfaces.size(),
                                interfaces.stream().filter(item -> item.getStatus() == InterfaceStatus.RUNNING).count(),
                                interfaces.stream().filter(item -> item.getStatus() == InterfaceStatus.WARNING).count(),
                                interfaces.stream().filter(item -> item.getStatus() == InterfaceStatus.FAILED).count(),
                                interfaces.stream().filter(item -> item.getStatus() == InterfaceStatus.PAUSED).count(),
                totalSuccess,
                totalFailure,
                successRate
        );
    }

        public List<ExecutionHistoryResponse> getRecentExecutions(int limit) {
                return executionHistoryRepository.findTop10ByOrderByExecutedAtDesc().stream()
                                .limit(limit)
                                .map(this::toExecutionResponse)
                                .toList();
        }

        public List<ExecutionHistoryResponse> getExecutionsByInterface(Long interfaceId) {
                return executionHistoryRepository.findTop20ByInterfaceIdOrderByExecutedAtDesc(interfaceId).stream()
                                .map(this::toExecutionResponse)
                                .toList();
        }

        @Transactional
    public void register(InterfaceRegistrationRequest request) {
                interfaceRepository.findByInterfaceCode(request.interfaceCode())
                                .ifPresent(existing -> {
                                        throw new IllegalArgumentException("이미 등록된 인터페이스 코드입니다: " + request.interfaceCode());
                                });

                InsuranceInterface entity = InsuranceInterface.builder()
                                .interfaceCode(request.interfaceCode())
                                .interfaceName(request.interfaceName())
                                .sourceSystem(request.sourceSystem())
                                .targetSystem(request.targetSystem())
                                .channelType(request.channelType())
                                .direction(request.direction())
                                .status(InterfaceStatus.RUNNING)
                                .successCount(0)
                                .failureCount(0)
                                .lastExecutionAt(LocalDateTime.now())
                                .ownerTeam(request.ownerTeam())
                                .description(request.description())
                                .active(true)
                                .build();

                InsuranceInterface saved = interfaceRepository.save(entity);
                appendExecutionHistory(saved, ExecutionResultType.SUCCESS, "신규 인터페이스 등록", false);
    }

        @Transactional
        public InterfaceSummaryResponse update(Long id, InterfaceUpdateRequest request) {
                InsuranceInterface item = getInterface(id);
                item.setInterfaceName(request.interfaceName());
                item.setSourceSystem(request.sourceSystem());
                item.setTargetSystem(request.targetSystem());
                item.setChannelType(request.channelType());
                item.setDirection(request.direction());
                item.setStatus(request.status());
                item.setOwnerTeam(request.ownerTeam());
                item.setDescription(request.description());
                item.setActive(request.active());
                item.setLastExecutionAt(LocalDateTime.now());
                InsuranceInterface saved = interfaceRepository.save(item);
                appendExecutionHistory(saved, ExecutionResultType.SUCCESS, "인터페이스 정보 수정", false);
                return toSummary(saved);
        }

        @Transactional
        public InterfaceSummaryResponse changeStatus(Long id, InterfaceStatusUpdateRequest request) {
                InsuranceInterface item = getInterface(id);
                item.setStatus(request.status());
                item.setLastExecutionAt(LocalDateTime.now());
                InsuranceInterface saved = interfaceRepository.save(item);
                appendExecutionHistory(saved, request.status() == InterfaceStatus.FAILED ? ExecutionResultType.FAILURE : ExecutionResultType.SUCCESS,
                        "운영 상태 변경: " + request.status(), false);
                return toSummary(saved);
        }

        @Transactional
        public InterfaceSummaryResponse deactivate(Long id) {
                InsuranceInterface item = getInterface(id);
                item.setActive(false);
                item.setStatus(InterfaceStatus.PAUSED);
                item.setLastExecutionAt(LocalDateTime.now());
                InsuranceInterface saved = interfaceRepository.save(item);
                appendExecutionHistory(saved, ExecutionResultType.WARNING, "인터페이스 비활성화", false);
                return toSummary(saved);
        }

        @Transactional
        public InterfaceSummaryResponse retry(Long interfaceId) {
                InsuranceInterface item = getInterface(interfaceId);
                item.setStatus(InterfaceStatus.RUNNING);
                item.setSuccessCount(item.getSuccessCount() + 1);
                item.setLastExecutionAt(LocalDateTime.now());

                errorLogRepository.save(ErrorLog.builder()
                                .interfaceId(interfaceId)
                                .interfaceCode(item.getInterfaceCode())
                                .message("수동 재처리 수행")
                                .detail("운영자가 실패 건에 대해 수동 재처리를 수행했습니다.")
                                .occurredAt(LocalDateTime.now())
                                .retriable(false)
                                .build());

                InsuranceInterface saved = interfaceRepository.save(item);
                appendExecutionHistory(saved, ExecutionResultType.SUCCESS, "실패 건 수동 재처리", true);
                return toSummary(saved);
    }

    private InterfaceSummaryResponse toSummary(InsuranceInterface item) {
        return new InterfaceSummaryResponse(
                                item.getId(),
                                item.getInterfaceCode(),
                                item.getInterfaceName(),
                                item.getSourceSystem(),
                                item.getTargetSystem(),
                                item.getChannelType(),
                                item.getDirection(),
                                item.getStatus(),
                                item.getSuccessCount(),
                                item.getFailureCount(),
                                item.getLastExecutionAt(),
                                item.getOwnerTeam(),
                                item.getDescription(),
                                item.isActive()
                );
        }

        private ErrorLogResponse toLogResponse(ErrorLog log) {
                return new ErrorLogResponse(
                                log.getId(),
                                log.getInterfaceId(),
                                log.getInterfaceCode(),
                                log.getMessage(),
                                log.getDetail(),
                                log.getOccurredAt(),
                                log.isRetriable()
        );
    }

        private ExecutionHistoryResponse toExecutionResponse(InterfaceExecutionHistory history) {
                return new ExecutionHistoryResponse(
                                history.getId(),
                                history.getInterfaceId(),
                                history.getInterfaceCode(),
                                history.getStatus(),
                                history.getResultType(),
                                history.getMessage(),
                                history.getExecutedAt(),
                                history.isRetried()
                );
        }

        private void appendExecutionHistory(InsuranceInterface item,
                                                                                ExecutionResultType resultType,
                                                                                String message,
                                                                                boolean retried) {
                executionHistoryRepository.save(InterfaceExecutionHistory.builder()
                                .interfaceId(item.getId())
                                .interfaceCode(item.getInterfaceCode())
                                .status(item.getStatus())
                                .resultType(resultType)
                                .message(message)
                                .executedAt(LocalDateTime.now())
                                .retried(retried)
                                .build());
        }
}
