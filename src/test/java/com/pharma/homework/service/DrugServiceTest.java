package com.pharma.homework.service;

import com.pharma.homework.dto.NewDrugRequest;
import com.pharma.homework.model.Drug;
import com.pharma.homework.repository.DrugRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DrugServiceTest {

    @Mock
    DrugRepository drugRepository;

    @InjectMocks
    DrugService drugService;

    @Test
    void testAddNewDrugSuccessfully() {
        // given
        NewDrugRequest request = new NewDrugRequest("VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        Drug drug = Drug.from(request);
        drug.setId(1L);
        when(drugRepository.save(any())).thenReturn(drug);

        // when
        Drug result = drugService.addNewDrug(request);

        // then
        assertNotNull(result.getId());
        assertEquals(drug, result);
        verify(drugRepository, times(1)).save(any());
    }


}
