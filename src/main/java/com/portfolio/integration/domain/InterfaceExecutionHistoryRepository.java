package com.portfolio.integration.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterfaceExecutionHistoryRepository extends JpaRepository<InterfaceExecutionHistory, Long> {

    List<InterfaceExecutionHistory> findTop10ByOrderByExecutedAtDesc();

    List<InterfaceExecutionHistory> findTop20ByInterfaceIdOrderByExecutedAtDesc(Long interfaceId);
}
