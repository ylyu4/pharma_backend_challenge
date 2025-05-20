package com.pharma.homework.repository;


import com.pharma.homework.TestUtils;
import com.pharma.homework.model.AuditLog;
import com.pharma.homework.model.AuditLogDrugInfo;
import com.pharma.homework.model.AuditStatus;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.Prescription;
import com.pharma.homework.model.PrescriptionDrugInfo;

import com.pharma.homework.model.PrescriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.pharma.homework.model.PrescriptionStatus.FULFILLED;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;


    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setup() {
        createAuditLog(1L, 1L, AuditStatus.SUCCESS);
        createAuditLog(1L, 2L, AuditStatus.FAILURE);
        createAuditLog(3L, 2L, AuditStatus.SUCCESS);
    }

    private void createAuditLog(Long patientId, Long pharmacyId, AuditStatus status) {
        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", "Address", "Phone");
        Pharmacy presistedPharmacy = testEntityManager.persist(pharmacy);

        Drug drug = TestUtils.generateDrug("test", "test", "test", LocalDate.now().minusDays(10), 200);
        Drug presistedDrug = testEntityManager.persist(drug);

        PrescriptionDrugInfo drugInfo = new PrescriptionDrugInfo(presistedDrug, 5);
        Prescription prescription = new Prescription(
                presistedPharmacy,
                1L,
                FULFILLED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );
        drugInfo.setPrescription(prescription);
        prescription.getPrescriptionDrugs().add(drugInfo);
        Prescription persistedPrescription = testEntityManager.persist(prescription);

        AuditLogDrugInfo auditLogDrugInfo = new AuditLogDrugInfo(presistedDrug, 10, 30);
        AuditLog log = new AuditLog(persistedPrescription, patientId, pharmacyId, status, null, LocalDateTime.now(), new ArrayList<>());
        auditLogDrugInfo.setAuditLog(log);
        log.getAuditLogDrugs().add(auditLogDrugInfo);

        testEntityManager.persist(log);
    }


    @Test
    void test_save_prescription_successfully() {
        // given
        Pharmacy pharmacy = new Pharmacy("pharmacy1", "123 Main St", "1234567890");
        Pharmacy savedPharmacy = pharmacyRepository.save(pharmacy);

        Drug drug = TestUtils.generateDrug("test", "test", "test", LocalDateTime.now().toLocalDate().plusMonths(6), 100);
        Drug savedDrug = drugRepository.save(drug);

        Prescription prescription = new Prescription(
                savedPharmacy,
                999L,
                PrescriptionStatus.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );

        PrescriptionDrugInfo drugInfo = new PrescriptionDrugInfo(savedDrug, 10);
        drugInfo.setPrescription(prescription);
        prescription.getPrescriptionDrugs().add(drugInfo);

        // when
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // then
        assertNotNull(savedPrescription.getId());
        assertEquals(1, savedPrescription.getPrescriptionDrugs().size());
        assertEquals(savedDrug.getName(), savedPrescription.getPrescriptionDrugs().get(0).getDrug().getName());
    }



    @Test
    void find_by_conditions_no_filters() {
        // given
        List<AuditLog> result = auditLogRepository.findByConditions(null, null, null);

        //then
        assertEquals(3, result.size());
    }

    @Test
    void find_by_conditions_by_patientId() {
        // given
        List<AuditLog> result = auditLogRepository.findByConditions(1L, null, null);

        // then
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().allMatch(l -> l.getPatientId().equals(1L)))
        );
    }

    @Test
    void find_by_conditions_by_pharmacyId() {
        // given
        List<AuditLog> result = auditLogRepository.findByConditions(null, 2L, null);

        // then
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().allMatch(l -> l.getPharmacyId().equals(2L)))
        );
    }

    @Test
    void find_by_conditions_by_status() {
        // given
        List<AuditLog> result = auditLogRepository.findByConditions(null, null, AuditStatus.SUCCESS);

        // then
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().allMatch(l -> l.getStatus() == AuditStatus.SUCCESS))
        );
    }

    @Test
    void find_by_conditions_combined_filters() {
        // given
        List<AuditLog> result = auditLogRepository.findByConditions(
                3L,
                2L,
                AuditStatus.SUCCESS
        );

        // then
        assertEquals(1, result.size());
        AuditLog log = result.get(0);
        assertAll(
                () -> assertEquals(3L, log.getPatientId()),
                () -> assertEquals(2L, log.getPharmacyId()),
                () -> assertEquals(AuditStatus.SUCCESS, log.getStatus())
        );
    }
}
