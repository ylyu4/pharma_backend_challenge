package com.pharma.homework.service;

import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.exception.PharmacyDrugInfoNotFoundException;
import com.pharma.homework.exception.PharmacyNotFoundException;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import com.pharma.homework.repository.DrugRepository;
import com.pharma.homework.repository.PharmacyDrugInfoRepository;
import com.pharma.homework.repository.PharmacyRepository;
import com.pharma.homework.service.model.DrugStockUpdate;
import com.pharma.homework.service.model.PharmacyStockUpdate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockUpdateServiceTest {

    @Mock
    private DrugRepository drugRepository;

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PharmacyDrugInfoRepository pharmacyDrugInfoRepository;

    @InjectMocks
    private StockUpdateService stockUpdateService;

    @Test
    void should_update_drug_stock_successfully() {
        // given
        Long drugId = 1L;
        Drug drug = new Drug();
        drug.setId(drugId);
        drug.setVersion(0);
        DrugStockUpdate update = new DrugStockUpdate(drugId, 10);

        when(drugRepository.findById(drugId)).thenReturn(Optional.of(drug));
        when(drugRepository.decreaseStock(drugId, 10, 0)).thenReturn(1);

        // then
        assertDoesNotThrow(() -> stockUpdateService.updateDrugStocks(List.of(update)));
    }

    @Test
    void should_throw_when_update_drug_stock_fails() {
        // given
        Long drugId = 1L;
        Drug drug = new Drug();
        drug.setId(drugId);
        drug.setVersion(1);
        DrugStockUpdate update = new DrugStockUpdate(drugId, 10);

        when(drugRepository.findById(drugId)).thenReturn(Optional.of(drug));
        when(drugRepository.decreaseStock(drugId, 10, 1)).thenReturn(0); // Simulate conflict

        // then
        assertThrows(OptimisticLockingFailureException.class, () -> stockUpdateService.updateDrugStocks(List.of(update)));
    }

    @Test
    void should_update_pharmacy_stock_successfully() {
        // given
        Long pharmacyId = 1L;
        Long drugId = 2L;

        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(pharmacyId);
        Drug drug = new Drug();
        drug.setId(drugId);

        PharmacyDrugInfo info = new PharmacyDrugInfo();
        info.setVersion(1);
        info.setPharmacy(pharmacy);
        info.setDrug(drug);

        PharmacyStockUpdate update = new PharmacyStockUpdate(pharmacyId, drugId, 5);

        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.of(pharmacy));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(drug));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(pharmacy, drug)).thenReturn(Optional.of(info));
        when(pharmacyDrugInfoRepository.increaseDispensingAmount(pharmacyId, drugId, 5, 1)).thenReturn(1);

        // then
        assertDoesNotThrow(() -> stockUpdateService.updatePharmacyAllocations(List.of(update)));
    }

    @Test
    void should_throw_exception_when_update_pharmacy_stock_conflict() {
        // given
        Long pharmacyId = 1L;
        Long drugId = 2L;

        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(pharmacyId);
        Drug drug = new Drug();
        drug.setId(drugId);

        PharmacyDrugInfo info = new PharmacyDrugInfo();
        info.setVersion(1);
        info.setPharmacy(pharmacy);
        info.setDrug(drug);

        PharmacyStockUpdate update = new PharmacyStockUpdate(pharmacyId, drugId, 5);

        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.of(pharmacy));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(drug));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(pharmacy, drug)).thenReturn(Optional.of(info));
        when(pharmacyDrugInfoRepository.increaseDispensingAmount(pharmacyId, drugId, 5, 1)).thenReturn(0);

        // then
        assertThrows(OptimisticLockingFailureException.class, () -> stockUpdateService.updatePharmacyAllocations(List.of(update)));
    }

    @Test
    void should_throw_exception_when_drug_not_found() {
        // given
        Long drugId = 999L;
        DrugStockUpdate update = new DrugStockUpdate(drugId, 5);
        when(drugRepository.findById(drugId)).thenReturn(Optional.empty());

        // then
        assertThrows(DrugNotFoundException.class, () -> stockUpdateService.updateDrugStocks(List.of(update)));
    }

    @Test
    void should_throw_exception_when_pharmacy_or_drug_not_found() {
        // given
        Long pharmacyId = 1L;
        Long drugId = 2L;
        PharmacyStockUpdate update = new PharmacyStockUpdate(pharmacyId, drugId, 5);

        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.empty());
        assertThrows(PharmacyNotFoundException.class, () -> stockUpdateService.updatePharmacyAllocations(List.of(update)));

        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(pharmacyId);
        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.of(pharmacy));
        when(drugRepository.findById(drugId)).thenReturn(Optional.empty());

        // then
        assertThrows(DrugNotFoundException.class, () -> stockUpdateService.updatePharmacyAllocations(List.of(update)));
    }

    @Test
    void should_throw_exception_when_pharmacy_drug_info_missing() {
        // given
        Long pharmacyId = 1L;
        Long drugId = 2L;
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(pharmacyId);
        Drug drug = new Drug();
        drug.setId(drugId);

        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.of(pharmacy));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(drug));
        when(pharmacyDrugInfoRepository.findByPharmacyAndDrug(pharmacy, drug)).thenReturn(Optional.empty());

        PharmacyStockUpdate update = new PharmacyStockUpdate(pharmacyId, drugId, 5);

        // then
        assertThrows(PharmacyDrugInfoNotFoundException.class, () -> stockUpdateService.updatePharmacyAllocations(List.of(update)));
    }
}