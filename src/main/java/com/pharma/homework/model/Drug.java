package com.pharma.homework.model;


import com.pharma.homework.dto.request.CreateDrugRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDate;

@Entity
@Table(name = "drug")
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String manufacturer;

    // not sure with the format of batchNumber, need to confirm with BA, currently using String is more flexible
    private String batchNumber;

    private LocalDate expiryDate;

    private Integer stock;

    @Version
    private Integer version;

    public Drug() {
    }

    public Drug(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void addStock(Integer stock) {
        this.stock += stock;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public static Drug from(CreateDrugRequest request) {
        Drug drug = new Drug();
        drug.setName(request.name());
        drug.setManufacturer(request.manufacturer());
        drug.setBatchNumber(request.batchNumber());
        drug.setExpiryDate(request.expiryDate());
        drug.setStock(request.stock());
        return drug;
    }
}
