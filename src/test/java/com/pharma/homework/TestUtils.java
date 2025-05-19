package com.pharma.homework;

import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;

import java.time.LocalDate;

public class TestUtils {

    public static Drug generateDrug(Long id,
                                    String name,
                                    String manufacturer,
                                    String batchNumber,
                                    LocalDate expiryDate,
                                    Integer stock) {
        Drug drug = new Drug();
        drug.setId(id);
        drug.setName(name);
        drug.setManufacturer(manufacturer);
        drug.setBatchNumber(batchNumber);
        drug.setExpiryDate(expiryDate);
        drug.setStock(stock);
        return drug;
    }

    public static Drug generateDrug(String name,
                                    String manufacturer,
                                    String batchNumber,
                                    LocalDate expiryDate,
                                    Integer stock) {
        Drug drug = new Drug();
        drug.setName(name);
        drug.setManufacturer(manufacturer);
        drug.setBatchNumber(batchNumber);
        drug.setExpiryDate(expiryDate);
        drug.setStock(stock);
        return drug;
    }

    public static PharmacyDrugInfo generateDrugInfo(Pharmacy pharmacy,
                                                    Drug drug,
                                                    Integer dispensingAmount,
                                                    Integer maxAllocationAmount) {

        PharmacyDrugInfo drugInfo = new PharmacyDrugInfo();
        drugInfo.setPharmacy(pharmacy);
        drugInfo.setDrug(drug);
        drugInfo.setDispensingAmount(dispensingAmount);
        drugInfo.setMaxAllocationAmount(maxAllocationAmount);
        return drugInfo;
    }
}
