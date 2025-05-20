package com.pharma.homework.dto.response;

import java.time.LocalDate;

public record DrugInfoResponse(Long id, String name, String manufacturer, String batchNumber,
                               LocalDate expiryDate, Integer maxAllocationAmount, Integer dispensingAmount) {

}
