package com.pharma.homework.model;

import java.io.Serializable;

public class PharmacyDrugId implements Serializable {
    private Long pharmacy;
    private Long drug;

    public Long getPharmacy() {
        return pharmacy;
    }

    public Long getDrug() {
        return drug;
    }
}