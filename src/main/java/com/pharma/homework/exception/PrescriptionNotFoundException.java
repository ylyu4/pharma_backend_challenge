package com.pharma.homework.exception;

public class PrescriptionNotFoundException extends RuntimeException {

    public PrescriptionNotFoundException(Long prescriptionId) {
        super("Prescription is not found with this ID: " + prescriptionId);
    }

}
