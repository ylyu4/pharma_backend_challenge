package com.pharma.homework.mapper;

import com.pharma.homework.dto.response.DrugResponse;
import com.pharma.homework.model.Drug;

public class DrugMapper {

    public static DrugResponse toResponse(Drug drug) {
        return new DrugResponse(
                drug.getId(),
                drug.getName(),
                drug.getManufacturer(),
                drug.getBatchNumber(),
                drug.getExpiryDate(),
                drug.getStock());
    }
}
