package com.pharma.homework.dto.response;

import java.time.LocalDate;

public class DrugResponse {

    private Long id;

    private String name;

    private String manufacturer;

    private String batchNumber;

    private LocalDate expiryDate;

    private Integer stock;

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Integer getStock() {
        return stock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public DrugResponse(Long id, String name, String manufacturer, String batchNumber, LocalDate expiryDate, Integer stock) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.stock = stock;
    }


}
