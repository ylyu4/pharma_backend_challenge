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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockUpdateService {

    private final DrugRepository drugRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PharmacyDrugInfoRepository pharmacyDrugInfoRepository;

    public StockUpdateService(DrugRepository drugRepository,
                              PharmacyRepository pharmacyRepository,
                              PharmacyDrugInfoRepository pharmacyDrugInfoRepository) {
        this.drugRepository = drugRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyDrugInfoRepository = pharmacyDrugInfoRepository;
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public void updateDrugStocks(List<DrugStockUpdate> updates) {
        for (DrugStockUpdate update : updates) {
            Long drugId = update.drugId();
            Drug drug = drugRepository.findById(drugId).orElseThrow(() -> new DrugNotFoundException(update.drugId()));
            int updated = drugRepository.decreaseStock(update.drugId(), update.quantity(), drug.getVersion());
            if (updated == 0) {
                throw new OptimisticLockingFailureException("Drug stock update failed due to version conflict");
            }
        }
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public void updatePharmacyAllocations(List<PharmacyStockUpdate> updates) {
        for (PharmacyStockUpdate update : updates) {
            Pharmacy pharmacy = pharmacyRepository.findById(update.pharmacyId())
                    .orElseThrow(() -> new PharmacyNotFoundException(update.pharmacyId()));
            Drug drug = drugRepository.findById(update.drugId())
                    .orElseThrow(() -> new DrugNotFoundException(update.drugId()));
            PharmacyDrugInfo pharmacyDrugInfo = pharmacyDrugInfoRepository
                    .findByPharmacyAndDrug(pharmacy, drug)
                    .orElseThrow(() -> new PharmacyDrugInfoNotFoundException(update.pharmacyId(), update.drugId()));

            int updated = pharmacyDrugInfoRepository.increaseDispensingAmount(
                    update.pharmacyId(), update.drugId(), update.quantity(), pharmacyDrugInfo.getVersion());

            if (updated == 0) {
                throw new OptimisticLockingFailureException("Pharmacy stock update failed due to version conflict");
            }
        }
    }
}

