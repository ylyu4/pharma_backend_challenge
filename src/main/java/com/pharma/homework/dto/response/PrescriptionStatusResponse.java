package com.pharma.homework.dto.response;

import com.pharma.homework.model.PrescriptionStatus;

public class PrescriptionStatusResponse {

    private Long prescriptionId;

    private PrescriptionStatus prescriptionStatus;

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public PrescriptionStatus getPrescriptionStatus() {
        return prescriptionStatus;
    }

    public void setPrescriptionStatus(PrescriptionStatus prescriptionStatus) {
        this.prescriptionStatus = prescriptionStatus;
    }

    public PrescriptionStatusResponse(Long prescriptionId, PrescriptionStatus prescriptionStatus) {
        this.prescriptionId = prescriptionId;
        this.prescriptionStatus = prescriptionStatus;
    }
}
