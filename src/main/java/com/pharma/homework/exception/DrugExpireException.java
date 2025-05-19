package com.pharma.homework.exception;

public class DrugExpireException extends RuntimeException {

    public DrugExpireException(Long drugId) {
        super("Drug is expired with this ID: " + drugId);
    }

}
