package com.pharma.homework.service;

import com.pharma.homework.TestUtils;
import com.pharma.homework.dto.response.AuditLogResponse;
import com.pharma.homework.model.AuditLog;
import com.pharma.homework.model.AuditLogDrugInfo;
import com.pharma.homework.model.AuditStatus;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.Prescription;
import com.pharma.homework.model.PrescriptionDrugInfo;
import com.pharma.homework.model.PrescriptionStatus;
import com.pharma.homework.repository.AuditLogRepository;
import com.pharma.homework.repository.DrugRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private AuditLogService auditLogService;


    @Test
    void save_log_prescription() {
        // 准备测试数据
        Prescription prescription = TestUtils.generatePrescription(PrescriptionStatus.FULFILLED);

        // 模拟药品查询
        when(drugRepository.findById(1L)).thenReturn(Optional.of(prescription.getPrescriptionDrugs().get(0).getDrug()));

        // 执行测试
        auditLogService.saveLogPrescription(prescription, AuditStatus.SUCCESS, null);

        // 验证结果
        ArgumentCaptor<AuditLog> logCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(logCaptor.capture());

        AuditLog savedLog = logCaptor.getValue();
        assertAll(
                () -> assertEquals(1L, savedLog.getPatientId()),
                () -> assertEquals(1L, savedLog.getPharmacyId()),
                () -> assertEquals(AuditStatus.SUCCESS, savedLog.getStatus()),
                () -> assertNull(savedLog.getErrorMessage()),
                () -> assertEquals(1, savedLog.getAuditLogDrugs().size()),
                () -> assertEquals(5, savedLog.getAuditLogDrugs().get(0).getQuantityDispensed())
        );
    }

    @Test
    void getAuditLogs_WithFilters() {
        Prescription prescription = TestUtils.generatePrescription(PrescriptionStatus.FULFILLED);
        Drug drug = TestUtils.generateDrug(1L, "test", "test", "test", LocalDate.now().minusDays(10), 200);
        AuditLogDrugInfo auditLogDrugInfo = new AuditLogDrugInfo(drug, 10, 30);
        AuditLog log1 = new AuditLog(prescription, 1L, 1L, AuditStatus.SUCCESS, null, LocalDateTime.now().minusDays(2), List.of(auditLogDrugInfo));
        AuditLog log2 = new AuditLog(prescription, 1L, 1L, AuditStatus.FAILURE, "insufficient stock", LocalDateTime.now().minusDays(2), List.of(auditLogDrugInfo));

        // 模拟不同过滤条件
        when(auditLogRepository.findByConditions(1L, 1L, AuditStatus.SUCCESS))
                .thenReturn(List.of(log1));
        when(auditLogRepository.findByConditions(null, null, AuditStatus.FAILURE))
                .thenReturn(List.of(log2));

        // 测试组合条件
        List<AuditLogResponse> result1 = auditLogService.getAuditLogs(1L, 1L, AuditStatus.SUCCESS);
        assertEquals(1, result1.size());

        // 测试空条件
        List<AuditLogResponse> result2 = auditLogService.getAuditLogs(null, null, AuditStatus.FAILURE);
        assertEquals(1, result2.size());

        // 验证repository调用
        verify(auditLogRepository, times(1)).findByConditions(1L, 1L, AuditStatus.SUCCESS);
        verify(auditLogRepository, times(1)).findByConditions(null, null, AuditStatus.FAILURE);
    }


}
