package com.pharma.homework.service;

import com.pharma.homework.TestUtils;
import com.pharma.homework.dto.request.CreateDrugRequest;
import com.pharma.homework.dto.request.DrugAddRequest;
import com.pharma.homework.dto.response.DrugResponse;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.model.Drug;
import com.pharma.homework.repository.DrugRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DrugServiceTest {

    @Mock
    DrugRepository drugRepository;

    @InjectMocks
    DrugService drugService;

    @Test
    void test_create_new_drug_successfully() {
        // given
        CreateDrugRequest request = new CreateDrugRequest("VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        Drug drug = Drug.from(request);
        drug.setId(1L);
        when(drugRepository.save(any())).thenReturn(drug);

        // when
        DrugResponse result = drugService.createNewDrug(request);

        // then
        assertNotNull(result.id());
        verify(drugRepository, times(1)).save(any());
    }

    @Test
    void test_add_existing_drug_quantity_successfully() {
        // given
        DrugAddRequest request = new DrugAddRequest(30);
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 10);
        when(drugRepository.findById(1L)).thenReturn(Optional.of(drug));
        when(drugRepository.save(any())).thenReturn(drug);

        // when
        DrugResponse result = drugService.addDrug(1L, request);

        // then
        assertEquals(40, result.stock());
        verify(drugRepository, times(1)).save(any());
        verify(drugRepository, times(1)).findById(any());
    }

    @Test
    void testThrowExceptionWhenDrugIdIsNotFound() {
        // given
        DrugAddRequest request = new DrugAddRequest(30);
        when(drugRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(DrugNotFoundException.class, () -> drugService.addDrug(1L, request));
    }

}
