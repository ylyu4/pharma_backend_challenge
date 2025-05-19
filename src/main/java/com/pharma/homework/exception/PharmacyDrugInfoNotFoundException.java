package com.pharma.homework.exception;

public class PharmacyDrugInfoNotFoundException extends RuntimeException {

    public PharmacyDrugInfoNotFoundException(Long pharmacyId, Long drugId) {
        super("PharmacyDrugInfo is not found with this pharmacy ID: " + pharmacyId + " and drug ID: " + drugId);
    }

}
