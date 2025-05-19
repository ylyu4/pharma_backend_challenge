package com.pharma.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharma.homework.dto.request.CreateDrugRequest;
import com.pharma.homework.dto.request.DrugAddRequest;
import com.pharma.homework.dto.response.DrugResponse;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.mapper.DrugMapper;
import com.pharma.homework.model.Drug;
import com.pharma.homework.service.DrugService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrugController.class)
public class DrugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DrugMapper drugMapper;

    @MockBean
    private DrugService drugService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_drug_successfully() throws Exception {
        // given
        CreateDrugRequest request = new CreateDrugRequest("VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        Drug drug = Drug.from(request);
        drug.setId(1L);
        DrugResponse drugResponse = new DrugResponse(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        when(drugMapper.toResponse(any())).thenReturn(drugResponse);
        when(drugService.createNewDrug(any())).thenReturn(drugResponse);

        // then
        mockMvc.perform(post("/drug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("VitaminB"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"""
                    {
                        "name": " ",
                        "manufacturer": "Unknown",
                        "batchNumber": "x123456",
                        "expiryDate": "2024-12-31",
                        "stock": 100
                    }
                """, """
                    {
                        "manufacturer": "Unknown",
                        "batchNumber": "x123456",
                        "expiryDate": "2024-12-31",
                        "stock": 100
                    }
                ""","""
                    {
                        "name": "VitaminB",
                        "manufacturer": " ",
                        "batchNumber": "x123456",
                        "expiryDate": "2024-12-31",
                        "stock": 100
                    }
                ""","""
                    {
                        "name": "VitaminB",
                        "batchNumber": "x123456",
                        "expiryDate": "2024-12-31",
                        "stock": 100
                    }
                ""","""
                    {
                        "name": "VitaminB",
                        "manufacturer": "Unknown",
                        "batchNumber": " ",
                        "expiryDate": "2024-12-31",
                        "stock": 100
                    }
                ""","""
                    {
                        "name": "VitaminB",
                        "manufacturer": "Unknown",
                        "expiryDate": "2024-12-31",
                        "stock": 100
                    }
                ""","""
                    {
                        "name": "VitaminB",
                        "manufacturer": "Unknown",
                        "batchNumber": "x123456",
                        "stock": 100
                    }
                ""","""
                    {
                        "name": "VitaminB",
                        "manufacturer": "Unknown",
                        "batchNumber": "x123456",
                        "expiryDate": "2024-12-31",
                    }
                """,})
    void should_return_error_when_params_are_invalid(String name) throws Exception {
        mockMvc.perform(post("/drug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(name))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_add_drug_successfully() throws Exception {
        // given
        DrugAddRequest request = new DrugAddRequest(1L, 100);
        DrugResponse drugResponse = new DrugResponse(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 200);
        when(drugMapper.toResponse(any())).thenReturn(drugResponse);
        when(drugService.addDrug(any())).thenReturn(drugResponse);

        // then
        mockMvc.perform(put("/drug/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(200));
    }

    @ParameterizedTest
    @ValueSource(strings = {"""
                    {
                        "addedStock": 50
                    }
                """, """
                    {
                        "id": 1L
                    }
                """})
    void should_throw_error_when_params_are_invalid(String name) throws Exception {
        mockMvc.perform(put("/drug/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(name))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_throw_not_found_exception_when_id_does_not_exist() throws Exception {
        // given
        DrugAddRequest request = new DrugAddRequest(1L, 100);

        // when
        when(drugService.addDrug(any())).thenThrow(DrugNotFoundException.class);

        // then
        mockMvc.perform(put("/drug/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

}