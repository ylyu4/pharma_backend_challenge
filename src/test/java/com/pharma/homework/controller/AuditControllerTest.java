package com.pharma.homework.controller;

import com.pharma.homework.dto.response.AuditLogResponse;
import com.pharma.homework.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.pharma.homework.model.AuditStatus.FAILURE;
import static com.pharma.homework.model.AuditStatus.SUCCESS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditLogController.class)
public class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;


    @Test
    void should_return_audit_logs_by_query_params() throws Exception {
        // Given
        AuditLogResponse response1 = new AuditLogResponse(1L, 1L, 1L, 1L, SUCCESS, null, LocalDateTime.now().minusDays(2), List.of());
        AuditLogResponse response2 = new AuditLogResponse(2L, 1L, 1L, 1L, FAILURE, "Out of stock", LocalDateTime.now().minusDays(2), List.of());

        when(auditLogService.getAuditLogs(1L, 1L, SUCCESS)).thenReturn(List.of(response1));

        // When & Then
        mockMvc.perform(get("/audit-logs")
                        .param("patientId", "1")
                        .param("pharmacyId", "1")
                        .param("status", "SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].patientId").value(1));

        verify(auditLogService, times(1)).getAuditLogs(1L, 1L, SUCCESS);
    }

    @Test
    void should_return_all_audit_logs_when_no_param() throws Exception {
        List<AuditLogResponse> allLogs = List.of(
                new AuditLogResponse(1L, 1L, 1L, 1L, SUCCESS, null, LocalDateTime.now().minusDays(2), List.of()),
                new AuditLogResponse(2L, 2L, 2L, 1L, FAILURE, "error", LocalDateTime.now().minusDays(2), List.of())
        );

        when(auditLogService.getAuditLogs(null, null, null)).thenReturn(allLogs);

        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(auditLogService, times(1)).getAuditLogs(null, null, null);
    }
}
