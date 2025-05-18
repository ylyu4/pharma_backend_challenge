package com.pharma.homework.repository;

import com.pharma.homework.TestUtils;
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
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 10);

        // when
        Drug savedDrug = drugRepository.save(drug);

        // then
        assertNotNull(savedDrug.getId());
        assertEquals("VitaminB", savedDrug.getName());
    }

    @Test
    void testFindDrugById() {
        // given
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 10);
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