package com.portfolio.integration.service;

import com.portfolio.integration.domain.InterfaceChannelType;
import com.portfolio.integration.domain.InterfaceDirection;
import com.portfolio.integration.domain.InterfaceStatus;
import com.portfolio.integration.dto.InterfaceExecutionRequest;
import com.portfolio.integration.dto.InterfaceRegistrationRequest;
import com.portfolio.integration.dto.InterfaceSearchCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class InterfaceMonitoringServiceTest {

    @Autowired
    private InterfaceMonitoringService service;

    @Test
    void dashboardMetricsShouldBeCalculated() {
        var metrics = service.getDashboardMetrics();

        assertThat(metrics.totalInterfaces()).isGreaterThan(0);
        assertThat(metrics.totalSuccessCount()).isGreaterThan(0);
        assertThat(metrics.successRate()).isBetween(0.0, 100.0);
    }

    @Test
    void registerAndSearchShouldWork() {
        service.register(new InterfaceRegistrationRequest(
                "IF-TST-999",
                "테스트 인터페이스",
                "Test Source",
                "Test Target",
                InterfaceChannelType.REST_API,
                InterfaceDirection.INBOUND,
                "테스트팀",
                "등록/검색 테스트"
        ));

        var results = service.search(new InterfaceSearchCondition("IF-TST-999", null, null, true));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).interfaceName()).isEqualTo("테스트 인터페이스");
        assertThat(results.get(0).status()).isEqualTo(InterfaceStatus.RUNNING);
    }

    @Test
    void retryShouldUpdateStatusAndCounters() {
        var target = service.search(new InterfaceSearchCondition("IF-PAY-003", null, null, true)).get(0);
        int beforeSuccess = target.successCount();

        var retried = service.retry(target.id());

        assertThat(retried.status()).isEqualTo(InterfaceStatus.RUNNING);
        assertThat(retried.successCount()).isEqualTo(beforeSuccess + 1);
    }

    @Test
    void recordExecutionShouldUpdateFailureAndStatus() {
        var target = service.search(new InterfaceSearchCondition("IF-CLM-001", null, null, true)).get(0);
        int beforeFailure = target.failureCount();

        var updated = service.recordExecution(target.id(), new InterfaceExecutionRequest(
                com.portfolio.integration.domain.ExecutionResultType.FAILURE,
                "외부 인증서 오류",
                true
        ));

        assertThat(updated.status()).isEqualTo(InterfaceStatus.FAILED);
        assertThat(updated.failureCount()).isEqualTo(beforeFailure + 1);
    }
}
