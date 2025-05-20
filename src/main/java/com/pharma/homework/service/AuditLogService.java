package com.pharma.homework.service;

import com.pharma.homework.dto.response.AuditLogResponse;
import com.pharma.homework.mapper.AuditLogMapper;
import com.pharma.homework.model.AuditLog;
import com.pharma.homework.model.AuditLogDrugInfo;
import com.pharma.homework.model.AuditStatus;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Prescription;
import com.pharma.homework.model.PrescriptionDrugInfo;
import com.pharma.homework.repository.AuditLogRepository;
import com.pharma.homework.repository.DrugRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final DrugRepository drugRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, DrugRepository drugRepository) {
        this.auditLogRepository = auditLogRepository;
        this.drugRepository = drugRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLogPrescription(Prescription prescription, AuditStatus status, String errorMessage) {
        AuditLog auditLog = new AuditLog(prescription, prescription.getPatientId(), prescription.getPharmacy().getId(),
                status, errorMessage, LocalDateTime.now(), new ArrayList<>());
        List<AuditLogDrugInfo> drugInfos = prescription.getPrescriptionDrugs().stream()
                .map(pd -> {
                    AuditLogDrugInfo info = createAuditDrugInfo(pd, status);
                    info.setAuditLog(auditLog);
                    return info;
                })                .collect(Collectors.toList());
        auditLog.setAuditLogDrugs(drugInfos);
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponse> getAuditLogs(Long patientId, Long pharmacyId, AuditStatus status) {
        List<AuditLog> auditLogs = auditLogRepository.findByConditions(patientId, pharmacyId, status);
        return auditLogs.stream().map(AuditLogMapper::toResponse).toList();
    }

    private AuditLogDrugInfo createAuditDrugInfo(PrescriptionDrugInfo prescriptionDrug, AuditStatus status) {
        Drug drug = drugRepository.findById(prescriptionDrug.getDrug().getId())
                .orElseGet(() -> new Drug(prescriptionDrug.getDrug().getId()));

        return new AuditLogDrugInfo(
                drug,
                prescriptionDrug.getQuantity(),
                status == AuditStatus.SUCCESS ? prescriptionDrug.getQuantity() : 0
        );
    }
}