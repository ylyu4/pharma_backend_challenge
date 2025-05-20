package com.pharma.homework.service;

import com.pharma.homework.TestUtils;
import com.pharma.homework.dto.request.PrescriptionRequest;
import com.pharma.homework.dto.response.PrescriptionStatusResponse;
import com.pharma.homework.exception.DrugExpireException;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.exception.InsufficientGlobalStockException;
import com.pharma.homework.exception.InsufficientPharmacyStockException;
import com.pharma.homework.exception.InvalidPrescriptionStatusException;
import com.pharma.homework.exception.PharmacyNotFoundException;
import com.pharma.homework.exception.PrescriptionNotFoundException;
import com.pharma.homework.model.AuditStatus;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import com.pharma.homework.model.Prescription;
import com.pharma.homework.model.PrescriptionDrugInfo;
import com.pharma.homework.model.PrescriptionStatus;
import com.pharma.homework.repository.DrugRepository;
import com.pharma.homework.repository.PharmacyDrugInfoRepository;
import com.pharma.homework.repository.PharmacyRepository;
import com.pharma.homework.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrescriptionServiceTest {

    @Mock
    PrescriptionRepository prescriptionRepository;

    @Mock
    PharmacyRepository pharmacyRepository;

    @Mock
    DrugRepository drugRepository;

    @Mock
    PharmacyDrugInfoRepository pharmacyDrugInfoRepository;

    @Mock
    AuditLogService auditLogService;

    @Mock
    PrescriptionStatusService prescriptionStatusService;

    @Mock
    StockUpdateService stockUpdateService;


    @InjectMocks
    PrescriptionService prescriptionService;


    @Test
    void should_save_prescription_successfully() {
        // given
        Long pharmacyId = 1L;
        Long drugId = 1L;
        Integer quantity = 5;

        PrescriptionRequest request = new PrescriptionRequest(
                1L,
                pharmacyId,
                List.of(new PrescriptionRequest.DrugRequestInfo(drugId, quantity))
        );

        Pharmacy mockPharmacy = createPharmacy(pharmacyId);
        Drug validDrug = buildValidDrug();
        PharmacyDrugInfo validAllocation = createPharmacyDrugInfo(100, 50);

        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.of(mockPharmacy));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(validDrug));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(mockPharmacy, validDrug))
                .thenReturn(Optional.of(validAllocation));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // when
        PrescriptionStatusResponse response = prescriptionService.save(request);

        // then
        assertAll(
                () -> assertEquals(PrescriptionStatus.CREATED, response.prescriptionStatus()),
                () -> assertNotNull(response.prescriptionId()),
                () -> verify(prescriptionRepository).save(any(Prescription.class)),
                () -> verify(auditLogService, never()).saveLogPrescription(any(), eq(AuditStatus.FAILURE), any())
        );
    }

    @Test
    void should_throw_when_pharmacy_not_found() {
        // given
        Long invalidPharmacyId = 999L;
        PrescriptionRequest request = new PrescriptionRequest(
                1L,
                invalidPharmacyId,
                List.of(new PrescriptionRequest.DrugRequestInfo(100L, 1))
        );

        when(pharmacyRepository.findById(invalidPharmacyId)).thenReturn(Optional.empty());

        // then
        assertThrows(PharmacyNotFoundException.class, () -> prescriptionService.save(request));
    }

    @Test
    void should_throw_when_drug_not_found() {
        // given
        Long pharmacyId = 1L;
        Long invalidDrugId = 999L;

        PrescriptionRequest request = new PrescriptionRequest(
                1L,
                pharmacyId,
                List.of(new PrescriptionRequest.DrugRequestInfo(invalidDrugId, 1))
        );

        when(pharmacyRepository.findById(pharmacyId))
                .thenReturn(Optional.of(createPharmacy(pharmacyId)));
        when(drugRepository.findById(invalidDrugId)).thenReturn(Optional.empty());

        // then
        assertThrows(DrugNotFoundException.class,
                () -> prescriptionService.save(request));
    }

    @Test
    void should_throw_when_drug_expired() {
        // given
        Long pharmacyId = 1L;
        Long drugId = 1L;
        Drug expiredDrug = TestUtils.generateDrug(drugId, "test", "test", "test", LocalDate.now().minusDays(1), 100);


        PrescriptionRequest request = new PrescriptionRequest(
                1L,
                pharmacyId,
                List.of(new PrescriptionRequest.DrugRequestInfo(drugId, 1))
        );

        when(pharmacyRepository.findById(pharmacyId))
                .thenReturn(Optional.of(createPharmacy(pharmacyId)));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(expiredDrug));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(createPharmacyDrugInfo(100, 0)));

        // then
        assertThrows(DrugExpireException.class, () -> prescriptionService.save(request));
    }

    @Test
    void should_throw_when_insufficient_global_stock() {
        // given
        Long pharmacyId = 1L;
        Long drugId = 100L;
        Drug lowStockDrug = TestUtils.generateDrug(drugId, "test", "test", "test", LocalDate.now().plusDays(30), 5);
        
        PrescriptionRequest request = new PrescriptionRequest(
                1L,
                pharmacyId,
                List.of(new PrescriptionRequest.DrugRequestInfo(drugId, 10)) // 请求10个
        );

        when(pharmacyRepository.findById(pharmacyId))
                .thenReturn(Optional.of(createPharmacy(pharmacyId)));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(lowStockDrug));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(createPharmacyDrugInfo(100, 0)));

        // then
        assertThrows(InsufficientGlobalStockException.class,
                () -> prescriptionService.save(request));
    }

    @Test
    void should_throw_when_insufficient_pharmacy_allocation() {
        // given
        Long pharmacyId = 1L;
        Long drugId = 1L;
        PharmacyDrugInfo pharmacyDrugInfo = createPharmacyDrugInfo(100, 95);

        PrescriptionRequest request = new PrescriptionRequest(
                1L,
                pharmacyId,
                List.of(new PrescriptionRequest.DrugRequestInfo(drugId, 10))
        );

        when(pharmacyRepository.findById(pharmacyId))
                .thenReturn(Optional.of(createPharmacy(pharmacyId)));
        when(drugRepository.findById(drugId))
                .thenReturn(Optional.of(buildValidDrug()));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(pharmacyDrugInfo));

        // then
        assertThrows(InsufficientPharmacyStockException.class, () -> prescriptionService.save(request));
    }

    @Test
    void test_fulfill_prescription_successfully() {
        // given
        Drug validDrug = buildValidDrug();
        Prescription prescription = buildValidPrescriptionWithDrugs(
                PrescriptionStatus.CREATED,
                validDrug,
                5
        );
        PharmacyDrugInfo pharmacyDrugInfo = createPharmacyDrugInfo(100, 50);

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(pharmacyDrugInfo));
        doNothing().when(stockUpdateService).updateDrugStocks(any());
        doNothing().when(stockUpdateService).updatePharmacyAllocations(any());
        when(prescriptionRepository.save(prescription)).thenReturn(prescription);

        // when
        PrescriptionStatusResponse response = prescriptionService.fulfillPrescription(1L);

        // then
        assertAll(
                () -> assertEquals(PrescriptionStatus.FULFILLED, response.prescriptionStatus()),
                () -> verify(prescriptionRepository).save(prescription),
                () -> verify(auditLogService).saveLogPrescription(prescription, AuditStatus.SUCCESS, null)
        );
    }


    @Test
    void test_fulfill_prescription_not_exists() {
        // given
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(PrescriptionNotFoundException.class, () -> prescriptionService.fulfillPrescription(1L));
    }

    @Test
    void test_fulfill_prescription_is_invalid() {
        // given
        Prescription prescription = buildValidPrescription(PrescriptionStatus.FULFILLED);
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));

        // then
        assertThrows(InvalidPrescriptionStatusException.class, () -> prescriptionService.fulfillPrescription(1L));
    }

    @Test
    void test_fulfill_prescription_expired() {
        // given
        Prescription prescription = buildValidPrescription(PrescriptionStatus.CREATED);
        Drug expiredDrug = buildExpiredDrug();
        prescription.getPrescriptionDrugs().get(0).setDrug(expiredDrug);

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(buildValidPharmacyDrugInfo()));
        when(prescriptionStatusService.storeRejectPrescription(any()))
                .thenAnswer(invocation -> {
                    Prescription rejected = invocation.getArgument(0);
                    rejected.setStatus(PrescriptionStatus.REJECTED);
                    return rejected;
                });

        // then
        assertThrows(DrugExpireException.class, () -> prescriptionService.fulfillPrescription(1L));
        assertThat(prescription.getStatus()).isEqualTo(PrescriptionStatus.REJECTED);
    }

    @Test
    void test_fulfill_prescription_insufficient_global_stock() {
        // given
        Prescription prescription = buildValidPrescription(PrescriptionStatus.CREATED);
        Drug lowStockDrug = buildValidDrug();
        lowStockDrug.setStock(3);
        prescription.getPrescriptionDrugs().get(0).setDrug(lowStockDrug);

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(buildValidPharmacyDrugInfo()));
        doNothing().when(auditLogService).saveLogPrescription(any(), any(), any());
        when(prescriptionStatusService.storeRejectPrescription(any()))
                .thenAnswer(invocation -> {
                    Prescription rejected = invocation.getArgument(0);
                    rejected.setStatus(PrescriptionStatus.REJECTED);
                    return rejected;
                });

        // then
        assertThrows(InsufficientGlobalStockException.class, () -> prescriptionService.fulfillPrescription(1L));
        assertThat(prescription.getStatus()).isEqualTo(PrescriptionStatus.REJECTED);
    }

    @Test
    void test_fulfill_prescription_insufficient_pharmacy_stock() {
        // given
        Prescription prescription = buildValidPrescription(PrescriptionStatus.CREATED);
        PharmacyDrugInfo invalidAllocation = buildValidPharmacyDrugInfo();
        invalidAllocation.setMaxAllocationAmount(5);
        invalidAllocation.setDispensingAmount(5);

        Prescription rejectedPrescription = new Prescription();
        rejectedPrescription.setStatus(PrescriptionStatus.REJECTED);

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(invalidAllocation));
        when(prescriptionStatusService.storeRejectPrescription(any()))
                .thenReturn(rejectedPrescription);
        doNothing().when(auditLogService).saveLogPrescription(any(), any(), any());

        // then
        assertThrows(InsufficientPharmacyStockException.class, () -> prescriptionService.fulfillPrescription(1L));

        verify(prescriptionStatusService).storeRejectPrescription(prescription);
        verify(auditLogService).saveLogPrescription(eq(rejectedPrescription), eq(AuditStatus.FAILURE), any(String.class));
    }


    @Test
    void test_fulfill_prescription_request_conflict() {
        // given
        Prescription prescription = buildValidPrescription(PrescriptionStatus.CREATED);
        PharmacyDrugInfo pharmacyDrugInfo = buildValidPharmacyDrugInfo();

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(pharmacyDrugInfo));
        doThrow(new OptimisticLockingFailureException("conflict"))
                .when(stockUpdateService).updateDrugStocks(any());

        when(prescriptionStatusService.storeRejectPrescription(any())).thenReturn(prescription);
        when(prescriptionStatusService.storeRejectPrescription(any()))
                .thenAnswer(invocation -> {
                    Prescription rejected = invocation.getArgument(0);
                    rejected.setStatus(PrescriptionStatus.REJECTED);
                    return rejected;
                });

        // when
        Exception ex = assertThrows(OptimisticLockingFailureException.class,
                () -> prescriptionService.fulfillPrescription(1L));

        // then
        assertThat(ex).isInstanceOf(OptimisticLockingFailureException.class);
        assertThat(prescription.getStatus()).isEqualTo(PrescriptionStatus.REJECTED);
        verify(auditLogService).saveLogPrescription(eq(prescription), eq(AuditStatus.FAILURE), any(String.class));
    }



    private Prescription buildValidPrescription(PrescriptionStatus status) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(1L);
        Drug drug = buildValidDrug();
        PrescriptionDrugInfo drugInfo = new PrescriptionDrugInfo(drug, 5);

        return new Prescription(pharmacy, 1L, status, LocalDateTime.now(), LocalDateTime.now(), List.of(drugInfo));
    }

    private PharmacyDrugInfo buildValidPharmacyDrugInfo() {
        PharmacyDrugInfo pharmacyDrugInfo = new PharmacyDrugInfo();
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(1L);
        Drug drug = new Drug();
        drug.setId(1L);
        pharmacyDrugInfo.setPharmacy(pharmacy);
        pharmacyDrugInfo.setDrug(drug);
        pharmacyDrugInfo.setMaxAllocationAmount(100);
        pharmacyDrugInfo.setDispensingAmount(50);
        return pharmacyDrugInfo;
    }

    private Drug buildValidDrug() {
        return TestUtils.generateDrug(1L, "TEST", "TEST", "TEST", LocalDate.now().plusMonths(6), 100);
    }

    private Drug buildExpiredDrug() {
        return TestUtils.generateDrug(1L, "TEST", "TEST", "TEST", LocalDate.now().minusDays(1), 100);
    }

    private Pharmacy createPharmacy(Long id) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(id);
        return pharmacy;
    }


    private Prescription buildValidPrescriptionWithDrugs(PrescriptionStatus status, Drug drug, int quantity) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(1L);

        PrescriptionDrugInfo drugInfo = new PrescriptionDrugInfo(drug, quantity);
        Prescription prescription = new Prescription(
                pharmacy,
                1L,
                status,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );

        drugInfo.setPrescription(prescription);
        prescription.getPrescriptionDrugs().add(drugInfo);

        return prescription;
    }

    private PharmacyDrugInfo createPharmacyDrugInfo(int maxAllocation, int dispensed) {
        PharmacyDrugInfo info = new PharmacyDrugInfo();
        info.setMaxAllocationAmount(maxAllocation);
        info.setDispensingAmount(dispensed);
        info.setVersion(0);
        return info;
    }
}
