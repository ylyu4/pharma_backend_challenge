package com.pharma.homework.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreateDrugRequest(
    @NotBlank(message = "Name can not be blank")
    String name,

    @NotBlank(message = "Manufacturer can not be blank")
    String manufacturer,

    @NotBlank(message = "Batch number can not be blank")
    String batchNumber,

    @NotNull(message = "Expiry date can not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate expiryDate,

    @NotNull(message = "Stock can not be null")
    Integer stock) {

}
