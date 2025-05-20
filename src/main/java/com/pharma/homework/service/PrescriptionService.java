package com.pharma.homework.service;


import com.pharma.homework.dto.request.PrescriptionRequest;
import com.pharma.homework.dto.response.PrescriptionStatusResponse;
import com.pharma.homework.exception.DrugExpireException;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.exception.InsufficientGlobalStockException;
import com.pharma.homework.exception.InsufficientPharmacyStockException;
import com.pharma.homework.exception.InvalidPrescriptionStatusException;
import com.pharma.homework.exception.PharmacyDrugInfoNotFoundException;
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
import com.pharma.homework.service.model.DrugStockUpdate;
import com.pharma.homework.service.model.PharmacyStockUpdate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final AuditLogService auditLogService;

    private final PrescriptionStatusService prescriptionStatusService;

    private final StockUpdateService stockUpdateService;


    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PharmacyRepository pharmacyRepository,
                               DrugRepository drugRepository,
                               PharmacyDrugInfoRepository pharmacyDrugInfoRepository,
                               AuditLogService auditLogService,
                               PrescriptionStatusService prescriptionStatusService,
                               StockUpdateService stockUpdateService) {
        this.prescriptionRepository = prescriptionRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.drugRepository = drugRepository;
        this.pharmacyDrugInfoRepository = pharmacyDrugInfoRepository;
        this.auditLogService = auditLogService;
        this.prescriptionStatusService = prescriptionStatusService;
        this.stockUpdateService = stockUpdateService;
    }

    @Transactional
    public PrescriptionStatusResponse save(PrescriptionRequest prescriptionRequest) {
        Long pharmacyId = prescriptionRequest.pharmacyId();
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId).orElseThrow(() ->
                new PharmacyNotFoundException(pharmacyId));
        List<PrescriptionRequest.DrugRequestInfo> drugRequests = prescriptionRequest.drugs();
        Prescription prescription = new Prescription(pharmacy,
                prescriptionRequest.patientId(),
                PrescriptionStatus.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>());
        List<PrescriptionDrugInfo> prescriptionDrugInfoList = new ArrayList<>();
        for (PrescriptionRequest.DrugRequestInfo drugRequest : drugRequests) {
            generateDrugInfoFromRequest(drugRequest, pharmacy, pharmacyId, prescription, prescriptionDrugInfoList);
        }
        prescription.setPrescriptionDrugs(prescriptionDrugInfoList);

        Prescription saved = prescriptionRepository.save(prescription);

        return new PrescriptionStatusResponse(saved.getId(), saved.getStatus());
    }

    @Transactional
    public PrescriptionStatusResponse fulfillPrescription(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        if (prescription.getStatus() != PrescriptionStatus.CREATED) {
            throw new InvalidPrescriptionStatusException(prescriptionId, prescription.getStatus());
        }

        List<DrugStockUpdate> drugStockUpdates = new ArrayList<>();
        List<PharmacyStockUpdate> pharmacyStockUpdates = new ArrayList<>();

        try {
            for (PrescriptionDrugInfo drugInfo : prescription.getPrescriptionDrugs()) {
                processPrescriptionDrugInfo(drugInfo, prescription, drugStockUpdates, pharmacyStockUpdates);
            }

            stockUpdateService.updateDrugStocks(drugStockUpdates);
            stockUpdateService.updatePharmacyAllocations(pharmacyStockUpdates);

            Prescription fulfillPrescription = updatePrescriptionToFulfillStatus(prescription);
            auditLogService.saveLogPrescription(fulfillPrescription, AuditStatus.SUCCESS, null);
            return new PrescriptionStatusResponse(prescriptionId, PrescriptionStatus.FULFILLED);
        } catch (Exception ex) {
            Prescription rejectedPrescription = prescriptionStatusService.storeRejectPrescription(prescription);
            auditLogService.saveLogPrescription(rejectedPrescription, AuditStatus.FAILURE, ex.getMessage());
            throw ex;
        }
    }

    private void generateDrugInfoFromRequest(PrescriptionRequest.DrugRequestInfo drugRequest,
                                             Pharmacy pharmacy,
                                             Long pharmacyId,
                                             Prescription prescription,
                                             List<PrescriptionDrugInfo> prescriptionDrugInfoList) {
        Long drugId = drugRequest.drugId();
        Drug drug = drugRepository.findById(drugId).orElseThrow(() -> new DrugNotFoundException(drugId));
        PharmacyDrugInfo pharmacyDrugInfo = pharmacyDrugInfoRepository.findByPharmacyAndDrug(pharmacy, drug).orElseThrow(() ->
                new PharmacyDrugInfoNotFoundException(pharmacyId, drugId));

        validateDrug(drug, pharmacyDrugInfo, drugRequest.quantity());

        PrescriptionDrugInfo prescriptionDrugInfo = new PrescriptionDrugInfo(drug, drugRequest.quantity());
        prescriptionDrugInfo.setPrescription(prescription);
        prescriptionDrugInfoList.add(prescriptionDrugInfo);
    }

    private void processPrescriptionDrugInfo(PrescriptionDrugInfo drugInfo,
                                             Prescription prescription,
                                             List<DrugStockUpdate> drugStockUpdates,
                                             List<PharmacyStockUpdate> pharmacyStockUpdates) {
        Drug drug = drugInfo.getDrug();
        Pharmacy pharmacy = prescription.getPharmacy();
        PharmacyDrugInfo pharmacyDrugInfo = pharmacyDrugInfoRepository
                .findByPharmacyAndDrug(prescription.getPharmacy(), drug).orElseThrow(() ->
                        new PharmacyDrugInfoNotFoundException(prescription.getPharmacy().getId(), drug.getId()));

        validateDrug(drug, pharmacyDrugInfo, drugInfo.getQuantity());

        drugStockUpdates.add(new DrugStockUpdate(drug.getId(), drugInfo.getQuantity()));
        pharmacyStockUpdates.add(new PharmacyStockUpdate(
                pharmacy.getId(), drug.getId(), drugInfo.getQuantity()));
    }

    private Prescription updatePrescriptionToFulfillStatus(Prescription prescription) {
        prescription.setStatus(PrescriptionStatus.FULFILLED);
        prescription.setUpdatedAt(LocalDateTime.now());
        return prescriptionRepository.save(prescription);
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
