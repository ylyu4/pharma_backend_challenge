package com.pharma.homework.mapper;


import com.pharma.homework.dto.response.DrugInfoResponse;
import com.pharma.homework.dto.response.PharmaciesResponse;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import org.springframework.stereotype.Component;

@Component
public class PharmacyDrugInfoMapper {

    public PharmaciesResponse toResponse(Pharmacy pharmacy) {
        return new PharmaciesResponse(
                pharmacy.getId(),
                pharmacy.getName(),
                pharmacy.getAddress(),
                pharmacy.getPhone(),
                pharmacy.getDrugInfoSet().stream().map(this::convertToDrugInfoResponse).toList()
        );
    }

    public DrugInfoResponse convertToDrugInfoResponse(PharmacyDrugInfo info) {
        return new DrugInfoResponse(
                info.getDrug().getId(),
                info.getDrug().getName(),
                info.getDrug().getManufacturer(),
                info.getDrug().getBatchNumber(),
                info.getDrug().getExpiryDate(),
                info.getMaxAllocationAmount(),
                info.getDispensingAmount()
        );
    }
}
