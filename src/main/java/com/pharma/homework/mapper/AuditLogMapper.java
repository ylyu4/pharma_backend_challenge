package com.pharma.homework.mapper;

import com.pharma.homework.dto.response.AuditLogDrugInfoResponse;
import com.pharma.homework.dto.response.AuditLogResponse;
import com.pharma.homework.model.AuditLog;
import com.pharma.homework.model.AuditLogDrugInfo;

import java.util.stream.Collectors;

public class AuditLogMapper {

    public static AuditLogResponse toResponse(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getPrescription().getId(),
                auditLog.getPatientId(),
                auditLog.getPharmacyId(),
                auditLog.getStatus(),
                auditLog.getErrorMessage(),
                auditLog.getCreatedAt(),
                auditLog.getAuditLogDrugs().stream().map(AuditLogMapper::toResponse).collect(Collectors.toList()));
    }

    private static AuditLogDrugInfoResponse toResponse(AuditLogDrugInfo auditLogDrugInfo) {
        return new AuditLogDrugInfoResponse(
                auditLogDrugInfo.getDrug().getId(),
                auditLogDrugInfo.getQuantityRequested(),
                auditLogDrugInfo.getQuantityDispensed());
    }
}
