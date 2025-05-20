package com.pharma.homework.dto.response;

import java.time.LocalDate;

public record DrugResponse(Long id, String name, String manufacturer, String batchNumber, LocalDate expiryDate, Integer stock) {

}
