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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class DrugRepositoryTest {

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void test_save_drug_successfully() {
        // given
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 10);

        // when
        Drug savedDrug = drugRepository.save(drug);

        // then
        assertNotNull(savedDrug.getId());
        assertEquals("VitaminB", savedDrug.getName());
    }

    @Test
    void test_find_drug_by_id() {
        // given
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 10);
        Drug saved = drugRepository.save(drug);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Drug> optionalDrug = drugRepository.findById(drug.getId());

        // then
        assertEquals(saved.getName(), optionalDrug.get().getName());
        assertEquals(saved.getManufacturer(), optionalDrug.get().getManufacturer());
    }
}