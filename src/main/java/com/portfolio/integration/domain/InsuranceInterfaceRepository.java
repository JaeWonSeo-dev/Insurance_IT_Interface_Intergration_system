package com.portfolio.integration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InsuranceInterfaceRepository extends JpaRepository<InsuranceInterface, Long>, JpaSpecificationExecutor<InsuranceInterface> {

    Optional<InsuranceInterface> findByInterfaceCode(String interfaceCode);
}
