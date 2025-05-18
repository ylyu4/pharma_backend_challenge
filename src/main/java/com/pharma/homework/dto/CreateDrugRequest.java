package com.pharma.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class CreateDrugRequest {
    @NotBlank(message = "Name can not be blank")
    private String name;

    @NotBlank(message = "Manufacturer can not be blank")
    private String manufacturer;

    @NotBlank(message = "Batch number can not be blank")
    private String batchNumber;

    @NotNull(message = "Expiry date can not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @NotNull(message = "Stock can not be null")
    private Integer stock;

    public CreateDrugRequest() {
    }

    public CreateDrugRequest(String name, String manufacturer, String batchNumber, LocalDate expiryDate, Integer stock) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public Integer getStock() {
        return stock;
    }

}
