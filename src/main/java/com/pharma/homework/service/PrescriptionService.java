package com.pharma.homework.service;


import com.pharma.homework.dto.request.PrescriptionRequest;
import com.pharma.homework.dto.response.PrescriptionStatusResponse;
import com.pharma.homework.exception.DrugExpireException;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.exception.InsufficientGlobalStockException;
import com.pharma.homework.exception.InsufficientPharmacyStockException;
import com.pharma.homework.exception.PharmacyDrugInfoNotFoundException;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    private final PharmacyRepository pharmacyRepository;

    private final DrugRepository drugRepository;

    private final PharmacyDrugInfoRepository pharmacyDrugInfoRepository;


    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PharmacyRepository pharmacyRepository,
                               DrugRepository drugRepository, PharmacyDrugInfoRepository pharmacyDrugInfoRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.drugRepository = drugRepository;
        this.pharmacyDrugInfoRepository = pharmacyDrugInfoRepository;
    }

    public PrescriptionStatusResponse save(PrescriptionRequest prescriptionRequest) {
        Long pharmacyId = prescriptionRequest.getPharmacyId();
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId).orElseThrow(() ->
                new PharmacyNotFoundException(pharmacyId));
        List<PrescriptionRequest.DrugRequestInfo> drugRequests = prescriptionRequest.getDrugs();
        List<PrescriptionDrugInfo> prescriptionDrugInfoList = new ArrayList<>();
        for (PrescriptionRequest.DrugRequestInfo drugRequest : drugRequests) {
            Long drugId = drugRequest.getDrugId();
            Drug drug = drugRepository.findById(drugId).orElseThrow(() -> new DrugNotFoundException(drugId));
            PharmacyDrugInfo pharmacyDrugInfo = pharmacyDrugInfoRepository.findByPharmacyAndDrug(pharmacy, drug).orElseThrow(() ->
                 new PharmacyDrugInfoNotFoundException(pharmacyId, drugId));
            validateDrug(drug, pharmacyDrugInfo, drugRequest.getQuantity());
            prescriptionDrugInfoList.add(new PrescriptionDrugInfo(drug, drugRequest.getQuantity()));
        }
        Prescription prescription = new Prescription(pharmacy,
                prescriptionRequest.getPatientId(),
                PrescriptionStatus.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                prescriptionDrugInfoList);

        Prescription saved = prescriptionRepository.save(prescription);

        return new PrescriptionStatusResponse(saved.getId(), saved.getStatus());
    }


    private void validateDrug(Drug drug, PharmacyDrugInfo pharmacyDrugInfo, Integer requestQuantity) {
        if (drug.getExpiryDate().isBefore(LocalDate.now())) {
            throw new DrugExpireException(drug.getId());
        }

        if (drug.getStock() < requestQuantity) {
            throw new InsufficientGlobalStockException(drug.getId());
        }

        if (pharmacyDrugInfo.getMaxAllocationAmount() < pharmacyDrugInfo.getDispensingAmount() + requestQuantity) {
            throw new InsufficientPharmacyStockException(drug.getId());
        }

    }
}
