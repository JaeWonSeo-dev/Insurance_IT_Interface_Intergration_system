package com.portfolio.integration.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interface_execution_history")
public class InterfaceExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long interfaceId;

    @Column(nullable = false, length = 40)
    private String interfaceCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterfaceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExecutionResultType resultType;

    @Column(nullable = false, length = 300)
    private String message;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    @Column(nullable = false)
    private boolean retried;
}
