package com.pharma.homework.exception;

public class InsufficientGlobalStockException extends RuntimeException {

    public InsufficientGlobalStockException(Long drugId) {
        super("Insufficient global stock of drug with this ID: " + drugId);
    }
}
