package com.pharma.homework.repository;

import com.pharma.homework.TestUtils;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PharmacyDrugInfoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PharmacyDrugInfoRepository pharmacyDrugInfoRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Test
    void should_save_and_retrieve_pharmacy_drug_info() {
        // given
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName("Pharmacy1");
        pharmacy.setAddress("Address1");
        pharmacy.setPhone("Phone1");

        Drug drug = TestUtils.generateDrug("test", "test", "test", LocalDate.now().plusYears(1), 100);

        drugRepository.save(drug);
        pharmacyRepository.save(pharmacy);

        entityManager.flush();

        // when
        PharmacyDrugInfo info = new PharmacyDrugInfo();
        info.setPharmacy(pharmacy);
        info.setDrug(drug);
        info.setMaxAllocationAmount(50);
        info.setDispensingAmount(10);
        PharmacyDrugInfo saved = pharmacyDrugInfoRepository.save(info);

        // then
        assertEquals(50, saved.getMaxAllocationAmount());
    }

    @Test
    void should_find_by_pharmacy_and_drug() {
        // given
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName("Pharmacy1");
        pharmacy.setAddress("Address1");
        pharmacy.setPhone("Phone1");
        Pharmacy savedPharmacy = pharmacyRepository.save(pharmacy);
        Drug drug = TestUtils.generateDrug("test", "test", "test", LocalDate.now().plusYears(1), 100);
        Drug savedDrug = drugRepository.save(drug);
        PharmacyDrugInfo info = new PharmacyDrugInfo();
        info.setPharmacy(savedPharmacy);
        info.setDrug(savedDrug);
        info.setMaxAllocationAmount(100);
        info.setDispensingAmount(20);
        pharmacyDrugInfoRepository.save(info);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<PharmacyDrugInfo> result = pharmacyDrugInfoRepository.findByPharmacyAndDrug(pharmacy, drug);

        // then
        assertTrue(result.isPresent());
        assertEquals(100, result.get().getMaxAllocationAmount());
    }

}