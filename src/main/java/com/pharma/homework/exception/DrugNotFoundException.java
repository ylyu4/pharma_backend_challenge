package com.pharma.homework.exception;

public class DrugNotFoundException extends  RuntimeException {

    public DrugNotFoundException(Long drugId) {
        super("Drug is not found with this ID: " + drugId);
    }

}
