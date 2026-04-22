package com.portfolio.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.integration.domain.InterfaceChannelType;
import com.portfolio.integration.domain.InterfaceDirection;
import com.portfolio.integration.dto.InterfaceRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class InterfaceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getDashboardShouldReturnMetrics() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.metrics.totalInterfaces").isNumber())
                                .andExpect(jsonPath("$.metrics.successRate").isNumber())
                                .andExpect(jsonPath("$.recentExecutions").isArray());
    }

    @Test
    void createInterfaceAndFetchListShouldWork() throws Exception {
        InterfaceRegistrationRequest request = new InterfaceRegistrationRequest(
                "IF-API-777",
                "API 생성 테스트",
                "API Source",
                "API Target",
                InterfaceChannelType.SOAP,
                InterfaceDirection.OUTBOUND,
                "API운영팀",
                "API 등록 테스트"
        );

        mockMvc.perform(post("/api/interfaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("신규 인터페이스가 등록되었습니다."));

        mockMvc.perform(get("/api/interfaces").param("keyword", "IF-API-777"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].interfaceCode").value("IF-API-777"));
    }

    @Test
    void retryEndpointShouldReturnUpdatedInterface() throws Exception {
        mockMvc.perform(post("/api/interfaces/3/retry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }

        @Test
        void executionsEndpointShouldReturnExecutionHistory() throws Exception {
                mockMvc.perform(get("/api/executions"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].interfaceCode").isNotEmpty())
                                .andExpect(jsonPath("$[0].resultType").isNotEmpty());
        }

        @Test
        void executeEndpointShouldUpdateFailureCountAndStatus() throws Exception {
                String payload = """
                                {
                                  "resultType": "FAILURE",
                                  "message": "외부 시스템 타임아웃",
                                  "retriable": true
                                }
                                """;

                mockMvc.perform(post("/api/interfaces/1/execute")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(payload))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value("FAILED"));
        }

        @Test
        void deleteEndpointShouldDeactivateInterface() throws Exception {
                mockMvc.perform(delete("/api/interfaces/4"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(4))
                                .andExpect(jsonPath("$.status").value("PAUSED"))
                                .andExpect(jsonPath("$.active").value(false));
        }
}
