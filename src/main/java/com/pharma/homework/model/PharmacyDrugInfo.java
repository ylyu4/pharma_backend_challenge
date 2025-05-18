package com.pharma.homework.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@IdClass(PharmacyDrugId.class)
@Table(name = "pharmacy_drug_info")
public class PharmacyDrugInfo {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(name = "max_allocation_amount", nullable = false)
    private Integer maxAllocationAmount;

    @Column(name = "dispensing_amount", nullable = false)
    private Integer dispensingAmount;


    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public Drug getDrug() {
        return drug;
    }

    public Integer getMaxAllocationAmount() {
        return maxAllocationAmount;
    }

    public Integer getDispensingAmount() {
        return dispensingAmount;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public void setMaxAllocationAmount(Integer maxAllocationAmount) {
        this.maxAllocationAmount = maxAllocationAmount;
    }

    public void setDispensingAmount(Integer dispensingAmount) {
        this.dispensingAmount = dispensingAmount;
    }
}
