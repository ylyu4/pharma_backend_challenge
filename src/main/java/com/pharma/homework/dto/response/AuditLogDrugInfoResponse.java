package com.pharma.homework.dto.response;

public record AuditLogDrugInfoResponse(Long drugId, Integer quantityRequested, Integer quantityDispensed) {}
