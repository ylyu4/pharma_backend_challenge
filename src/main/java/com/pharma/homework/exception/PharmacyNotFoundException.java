package com.pharma.homework.exception;

public class PharmacyNotFoundException extends RuntimeException {

    public PharmacyNotFoundException(Long pharmacyId) {
        super("Pharmacy is not found with this ID: " + pharmacyId);
    }

}
