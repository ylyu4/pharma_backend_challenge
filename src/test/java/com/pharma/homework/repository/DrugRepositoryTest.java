package com.pharma.homework.repository;

import com.pharma.homework.model.Drug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class DrugRepositoryTest {

    @Autowired
    private DrugRepository drugRepository;

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
}