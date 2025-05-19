package com.pharma.homework.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PrescriptionRequest {

    @NotNull(message = "patientId can not be null")
    private Long patientId;

    @NotNull(message = "pharmacyId can not be null")
    private Long pharmacyId;

    @Valid
    @NotEmpty(message = "drug list can not be empty")
    private List<DrugRequestInfo> drugs;


    public static class DrugRequestInfo {
        @NotNull(message = "drug ID can not be null")
        private Long drugId;

        @NotNull(message = "quantity can not be null")
        @Min(value = 1, message = "quantity must be at least 1")
        private Integer quantity;

        public Long getDrugId() {
            return drugId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setDrugId(Long drugId) {
            this.drugId = drugId;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public DrugRequestInfo(Long drugId, Integer quantity) {
            this.drugId = drugId;
            this.quantity = quantity;
        }
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getPharmacyId() {
        return pharmacyId;
    }

    public List<DrugRequestInfo> getDrugs() {
        return drugs;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setPharmacyId(Long pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public void setDrugs(List<DrugRequestInfo> drugs) {
        this.drugs = drugs;
    }

    public PrescriptionRequest(Long patientId, Long pharmacyId, List<DrugRequestInfo> drugs) {
        this.patientId = patientId;
        this.pharmacyId = pharmacyId;
        this.drugs = drugs;
    }
}




