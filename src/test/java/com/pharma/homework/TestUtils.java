package com.pharma.homework;

import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import com.pharma.homework.model.Prescription;
import com.pharma.homework.model.PrescriptionDrugInfo;
import com.pharma.homework.model.PrescriptionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        drug.setVersion(0);
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
        drug.setVersion(0);
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


    public static Prescription generatePrescription(PrescriptionStatus status) {
        Pharmacy pharmacy = new Pharmacy(1L, "Test Pharmacy", "Address", "Phone");
        Drug drug = TestUtils.generateDrug(1L, "Test Drug", "test", "test",
                LocalDate.now().plusMonths(6), 100);

        PrescriptionDrugInfo drugInfo = new PrescriptionDrugInfo(drug, 5);

        return new Prescription(
                pharmacy,
                1L,
                status,
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(drugInfo)
        );
    }
}
