package com.pharma.homework.exception;

import com.pharma.homework.model.PrescriptionStatus;

public class InvalidPrescriptionStatusException extends RuntimeException {

    public InvalidPrescriptionStatusException(Long prescriptionId, PrescriptionStatus prescriptionStatus) {
        super("Prescription status is invalid with this ID: " + prescriptionId + ", current status: " + prescriptionStatus);
    }

}
