package com.pharma.homework.service;


import com.pharma.homework.model.Prescription;
import com.pharma.homework.model.PrescriptionStatus;
import com.pharma.homework.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PrescriptionStatusService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionStatusService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 开一个新事务来保存log，与外部事务隔离
    public Prescription storeRejectPrescription(Prescription prescription) {
        prescription.setStatus(PrescriptionStatus.REJECTED);
        prescription.setUpdatedAt(LocalDateTime.now());
        return prescriptionRepository.save(prescription);
    }
}
