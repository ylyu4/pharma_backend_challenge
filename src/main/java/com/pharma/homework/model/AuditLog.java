package com.pharma.homework.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "pharmacy_id", nullable = false)
    private Long pharmacyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "auditLog", cascade = CascadeType.ALL)
    private List<AuditLogDrugInfo> auditLogDrugs = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(Long pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<AuditLogDrugInfo> getAuditLogDrugs() {
        return auditLogDrugs;
    }

    public void setAuditLogDrugs(List<AuditLogDrugInfo> auditLogDrugs) {
        this.auditLogDrugs = auditLogDrugs;
    }

    public AuditLog() {
    }

    public AuditLog(Prescription prescription,
                    Long patientId,
                    Long pharmacyId,
                    AuditStatus status,
                    String errorMessage,
                    LocalDateTime createdAt,
                    List<AuditLogDrugInfo> auditLogDrugs) {
        this.prescription = prescription;
        this.patientId = patientId;
        this.pharmacyId = pharmacyId;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.auditLogDrugs = auditLogDrugs;
    }
}
