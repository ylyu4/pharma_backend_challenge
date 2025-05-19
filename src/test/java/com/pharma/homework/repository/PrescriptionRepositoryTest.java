package com.pharma.homework.repository;

import com.pharma.homework.TestUtils;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.Prescription;
import com.pharma.homework.model.PrescriptionDrugInfo;
import com.pharma.homework.model.PrescriptionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class PrescriptionRepositoryTest {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Test
    void test_save_prescription_successfully() {
        // given
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName("pharmacy1");
        pharmacy.setAddress("123 Main St");
        pharmacy.setPhone("1234567890");
        Pharmacy savedPharmacy = pharmacyRepository.save(pharmacy);

        Drug drug = TestUtils.generateDrug("test", "test", "test", LocalDateTime.now().toLocalDate().plusMonths(6), 100);
        Drug savedDrug = drugRepository.save(drug);

        Prescription prescription = new Prescription(
                savedPharmacy,
                999L,
                PrescriptionStatus.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );

        PrescriptionDrugInfo drugInfo = new PrescriptionDrugInfo(savedDrug, 10);
        drugInfo.setPrescription(prescription);
        prescription.getPrescriptionDrugs().add(drugInfo);

        // when
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // then
        assertNotNull(savedPrescription.getId());
        assertEquals(1, savedPrescription.getPrescriptionDrugs().size());
        assertEquals(savedDrug.getName(), savedPrescription.getPrescriptionDrugs().get(0).getDrug().getName());
    }
}