package com.pharma.homework.repository;

import com.pharma.homework.TestUtils;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class PharmacyRepositoryTest {

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void test_find_all_pharmacies_with_drug_info() {
        // given
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 10);
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(1L);
        pharmacy.setName("Pharmacy1");
        pharmacy.setAddress("Address1");
        pharmacy.setPhone("Phone1");
        PharmacyDrugInfo pharmacyDrugInfo = new PharmacyDrugInfo();
        pharmacyDrugInfo.setDrug(drug);
        pharmacyDrugInfo.setPharmacy(pharmacy);
        pharmacyDrugInfo.setDispensingAmount(50);
        pharmacyDrugInfo.setMaxAllocationAmount(200);
        pharmacy.setDrugInfoSet(Set.of(pharmacyDrugInfo));
        drugRepository.save(drug);
        pharmacyRepository.save(pharmacy);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Pharmacy> pharmacies = pharmacyRepository.findAllWithDrugInfo();

        // then
        assertEquals(1, pharmacies.size());
        assertEquals("Pharmacy1", pharmacies.get(0).getName());
        Set<PharmacyDrugInfo> drugInfoSet = pharmacies.get(0).getDrugInfoSet();
        assertEquals(1, drugInfoSet.size());
        PharmacyDrugInfo pharmacyDrugInfo1 = drugInfoSet.stream().findFirst().orElse(null);
        assertEquals(50, pharmacyDrugInfo1.getDispensingAmount());
        assertEquals(200, pharmacyDrugInfo1.getMaxAllocationAmount());
    }
}
