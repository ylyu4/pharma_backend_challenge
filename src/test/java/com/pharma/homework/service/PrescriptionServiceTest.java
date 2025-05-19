package com.pharma.homework.service;

import com.pharma.homework.TestUtils;
import com.pharma.homework.dto.request.PrescriptionRequest;
import com.pharma.homework.dto.response.PrescriptionStatusResponse;
import com.pharma.homework.exception.DrugExpireException;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.exception.InsufficientGlobalStockException;
import com.pharma.homework.exception.InsufficientPharmacyStockException;
import com.pharma.homework.exception.PharmacyNotFoundException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        Drug mockDrug = createValidDrug(drugId);
        PharmacyDrugInfo mockPharmacyDrugInfo = createPharmacyDrugInfo(100, 50);
        Prescription prescription = new Prescription(mockPharmacy, 1L, PrescriptionStatus.CREATED, LocalDateTime.now(), LocalDateTime.now(), List.of(new PrescriptionDrugInfo(mockDrug, 5)));

        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.of(mockPharmacy));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(mockDrug));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(mockPharmacy, mockDrug))
                .thenReturn(Optional.of(mockPharmacyDrugInfo));
        when(prescriptionRepository.save(any())).thenReturn(prescription);

        // when
        PrescriptionStatusResponse response = prescriptionService.save(request);

        // then
        assertEquals(PrescriptionStatus.CREATED, response.getPrescriptionStatus());
        verify(prescriptionRepository).save(any());
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
        Long drugId = 100L;
        PharmacyDrugInfo pharmacyDrugInfo = createPharmacyDrugInfo(100, 95);

        PrescriptionRequest request = new PrescriptionRequest(
                1L,
                pharmacyId,
                List.of(new PrescriptionRequest.DrugRequestInfo(drugId, 10))
        );

        when(pharmacyRepository.findById(pharmacyId))
                .thenReturn(Optional.of(createPharmacy(pharmacyId)));
        when(drugRepository.findById(drugId))
                .thenReturn(Optional.of(createValidDrug(drugId)));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(any(), any()))
                .thenReturn(Optional.of(pharmacyDrugInfo));

        // then
        assertThrows(InsufficientPharmacyStockException.class, () -> prescriptionService.save(request));
    }

    private Pharmacy createPharmacy(Long id) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(id);
        return pharmacy;
    }

    private Drug createValidDrug(Long id) {
        return TestUtils.generateDrug(id, "test", "test", "test", LocalDate.now().plusDays(30), 100);
    }

    private PharmacyDrugInfo createPharmacyDrugInfo(int maxAllocationAmount, int dispensingAmount) {
        PharmacyDrugInfo pharmacyDrugInfo = new PharmacyDrugInfo();
        pharmacyDrugInfo.setMaxAllocationAmount(maxAllocationAmount);
        pharmacyDrugInfo.setDispensingAmount(dispensingAmount);
        return pharmacyDrugInfo;
    }

}
