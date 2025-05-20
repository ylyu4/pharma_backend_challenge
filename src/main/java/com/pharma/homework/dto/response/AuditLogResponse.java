package com.pharma.homework.dto.response;

import com.pharma.homework.model.AuditStatus;

import java.time.LocalDateTime;
import java.util.List;

public record AuditLogResponse(Long id,
                               Long prescriptionId,
                               Long patientId,
                               Long pharmacyId,
                               AuditStatus status,
                               String errorMessage,
                               LocalDateTime createdAt,
                               List<AuditLogDrugInfoResponse> drugs
) {}
