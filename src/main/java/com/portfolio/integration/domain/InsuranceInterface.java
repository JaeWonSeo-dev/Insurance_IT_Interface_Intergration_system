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
@Table(name = "insurance_interface")
public class InsuranceInterface {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 40)
        private String interfaceCode;

        @Column(nullable = false, length = 120)
        private String interfaceName;

        @Column(nullable = false, length = 80)
        private String sourceSystem;

        @Column(nullable = false, length = 80)
        private String targetSystem;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private InterfaceChannelType channelType;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private InterfaceDirection direction;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private InterfaceStatus status;

        @Column(nullable = false)
        private int successCount;

        @Column(nullable = false)
        private int failureCount;

        @Column(nullable = false)
        private LocalDateTime lastExecutionAt;

        @Column(nullable = false, length = 80)
        private String ownerTeam;

        @Column(nullable = false, length = 2000)
        private String description;

        @Column(nullable = false)
        private boolean active;
}
