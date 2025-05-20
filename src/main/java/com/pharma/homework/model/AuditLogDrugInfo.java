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
@IdClass(AuditLogDrugInfo.AuditLogDrugId.class)
@Table(name = "audit_log_drug_info")
public class AuditLogDrugInfo {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_log_id", nullable = false)
    private AuditLog auditLog;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(name = "quantity_requested", nullable = false)
    private Integer quantityRequested;

    @Column(name = "quantity_dispensed", nullable = false)
    private Integer quantityDispensed;

    public AuditLogDrugInfo() {
    }

    public AuditLogDrugInfo(Drug drug, Integer quantityRequested, Integer quantityDispensed) {
        this.drug = drug;
        this.quantityRequested = quantityRequested;
        this.quantityDispensed = quantityDispensed;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }

    public void setAuditLog(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public Integer getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(Integer quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    public Integer getQuantityDispensed() {
        return quantityDispensed;
    }

    public void setQuantityDispensed(Integer quantityDispensed) {
        this.quantityDispensed = quantityDispensed;
    }

    static class AuditLogDrugId implements Serializable {
        private Long auditLog;
        private Long drug;

        public Long getAuditLog() {
            return auditLog;
        }

        public Long getDrug() {
            return drug;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AuditLogDrugId that = (AuditLogDrugId) o;
            return Objects.equals(auditLog, that.auditLog) && Objects.equals(drug, that.drug);
        }

        @Override
        public int hashCode() {
            return Objects.hash(auditLog, drug);
        }
    }
}
