package com.pharma.homework.controller;

import com.pharma.homework.dto.response.AuditLogResponse;
import com.pharma.homework.model.AuditStatus;
import com.pharma.homework.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long pharmacyId,
            @RequestParam(required = false) AuditStatus status) {

        List<AuditLogResponse> logs = auditLogService.getAuditLogs(patientId, pharmacyId, status);
        return ResponseEntity.ok(logs);
    }
}
