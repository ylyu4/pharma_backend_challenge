package com.pharma.homework.model;


import com.pharma.homework.dto.CreateDrugRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
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

    public static Drug from(CreateDrugRequest request) {
        Drug drug = new Drug();
        drug.setName(request.getName());
        drug.setManufacturer(request.getManufacturer());
        drug.setBatchNumber(request.getBatchNumber());
        drug.setExpiryDate(request.getExpiryDate());
        drug.setStock(request.getStock());
        return drug;
    }
}
