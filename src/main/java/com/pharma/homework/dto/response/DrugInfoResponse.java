package com.pharma.homework.dto.response;

import java.time.LocalDate;

public class DrugInfoResponse {

    private Long id;

    private String name;

    private String manufacturer;

    private String batchNumber;

    private LocalDate expiryDate;

    private Integer maxAllocationAmount;

    private Integer dispensingAmount;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Integer getMaxAllocationAmount() {
        return maxAllocationAmount;
    }

    public Integer getDispensingAmount() {
        return dispensingAmount;
    }

    public DrugInfoResponse(Long id,
                            String name,
                            String manufacturer,
                            String batchNumber,
                            LocalDate expiryDate,
                            Integer maxAllocationAmount,
                            Integer dispensingAmount) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.maxAllocationAmount = maxAllocationAmount;
        this.dispensingAmount = dispensingAmount;
    }
}
