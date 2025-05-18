package com.pharma.homework.repository;

import com.pharma.homework.model.Drug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class DrugRepositoryTest {

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSaveDrugSuccessfully() {
        // given
        Drug drug = new Drug();
        drug.setName("VitaminB");
        drug.setManufacturer("Unknown");

        // when
        Drug savedDrug = drugRepository.save(drug);

        // then
        assertNotNull(savedDrug.getId());
        assertEquals("VitaminB", savedDrug.getName());
    }

    @Test
    void testUpdateStock() {
        // given
        Drug drug = new Drug();
        drug.setId(1L);
        drug.setName("VitaminB");
        drug.setManufacturer("Unknown");
        drug.setBatchNumber("x123456");
        drug.setExpiryDate(LocalDate.now());
        drug.setStock(10);
        Drug saved = drugRepository.save(drug);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Drug> drugList = drugRepository.findDrugById(drug.getId());

        // then
        assertEquals(1, drugList.size());
        assertEquals(saved.getName(), drugList.get(0).getName());
        assertEquals(saved.getManufacturer(), drugList.get(0).getManufacturer());
    }
}