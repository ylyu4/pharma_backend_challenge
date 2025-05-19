package com.pharma.homework.exception;

public class InsufficientPharmacyStockException extends RuntimeException {

    public InsufficientPharmacyStockException(Long drugId) {
        super("Insufficient pharmacy stock of drug with this ID: " + drugId);
    }
}
