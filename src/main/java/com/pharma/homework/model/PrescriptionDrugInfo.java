package com.pharma.homework.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(PrescriptionDrugInfo.PrescriptionDrugId.class)
@Table(name = "prescription_drug_info")
public class PrescriptionDrugInfo {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(nullable = false)
    private Integer quantity;

    public PrescriptionDrugInfo() {
    }

    public PrescriptionDrugInfo(Drug drug, Integer quantity) {
        this.drug = drug;
        this.quantity = quantity;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public Drug getDrug() {
        return drug;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    static class PrescriptionDrugId implements Serializable {
        private Long prescription;
        private Long drug;

        public Long getPrescription() {
            return prescription;
        }

        public Long getDrug() {
            return drug;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PrescriptionDrugId that = (PrescriptionDrugId) o;
            return Objects.equals(prescription, that.prescription) && Objects.equals(drug, that.drug);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prescription, drug);
        }
    }
}
