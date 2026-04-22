package com.portfolio.integration.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "error_log")
public class ErrorLog {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Long interfaceId;

        @Column(nullable = false, length = 40)
        private String interfaceCode;

        @Column(nullable = false, length = 160)
        private String message;

        @Column(nullable = false, length = 3000)
        private String detail;

        @Column(nullable = false)
        private LocalDateTime occurredAt;

        @Column(nullable = false)
        private boolean retriable;
}
