package com.pharma.homework.service;

import com.pharma.homework.TestUtils;
import com.pharma.homework.dto.request.CreateDrugRequest;
import com.pharma.homework.dto.request.DrugAddRequest;
import com.pharma.homework.dto.response.DrugResponse;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.mapper.DrugMapper;
import com.pharma.homework.model.Drug;
import com.pharma.homework.repository.DrugRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DrugServiceTest {

    @Mock
    DrugRepository drugRepository;

    @Mock
    DrugMapper drugMapper;

    @InjectMocks
    DrugService drugService;

    @Test
    void testCreateNewDrugSuccessfully() {
        // given
        CreateDrugRequest request = new CreateDrugRequest("VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        Drug drug = Drug.from(request);
        drug.setId(1L);
        when(drugRepository.save(any())).thenReturn(drug);
        DrugResponse drugResponse = new DrugResponse(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        when(drugMapper.toResponse(any())).thenReturn(drugResponse);

        // when
        DrugResponse result = drugService.createNewDrug(request);

        // then
        assertNotNull(result.getId());
        assertEquals(drugResponse, result);
        verify(drugRepository, times(1)).save(any());
    }

    @Test
    void testAddExistingDrugQuantitySuccessfully() {
        // given
        DrugAddRequest request = new DrugAddRequest(1L, 30);
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 10);
        when(drugRepository.findDrugById(1L)).thenReturn(List.of(drug));
        when(drugRepository.save(any())).thenReturn(drug);
        DrugResponse drugResponse = new DrugResponse(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 40);
        when(drugMapper.toResponse(any())).thenReturn(drugResponse);


        // when
        DrugResponse result = drugService.addDrug(request);

        // then
        assertEquals(40, result.getStock());
        verify(drugRepository, times(1)).save(any());
        verify(drugRepository, times(1)).findDrugById(any());
    }

    @Test
    void testThrowExceptionWhenDrugIdIsNotFound() {
        // given
        DrugAddRequest request = new DrugAddRequest(1L, 30);
        when(drugRepository.findDrugById(1L)).thenReturn(List.of());

        // then
        assertThrows(DrugNotFoundException.class, () -> drugService.addDrug(request));
    }

}
