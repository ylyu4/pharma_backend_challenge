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
@Table(name = "prescription")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionDrugInfo> prescriptionDrugs = new ArrayList<>();

    public Prescription() {
    }

    public Prescription(Pharmacy pharmacy,
                        Long patientId,
                        PrescriptionStatus status,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt,
                        List<PrescriptionDrugInfo> prescriptionDrugs) {
        this.pharmacy = pharmacy;
        this.patientId = patientId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.prescriptionDrugs = prescriptionDrugs;
    }

    public Long getId() {
        return id;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public Long getPatientId() {
        return patientId;
    }

    public PrescriptionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<PrescriptionDrugInfo> getPrescriptionDrugs() {
        return prescriptionDrugs;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setStatus(PrescriptionStatus status) {
        this.status = status;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrescriptionDrugs(List<PrescriptionDrugInfo> prescriptionDrugs) {
        this.prescriptionDrugs = prescriptionDrugs;
    }
}
