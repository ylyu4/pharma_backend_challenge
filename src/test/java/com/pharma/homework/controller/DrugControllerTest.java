package com.pharma.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharma.homework.dto.NewDrugRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrugController.class)
public class DrugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DrugService drugService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_add_drug_successfully() throws Exception {
        // given
        NewDrugRequest request = new NewDrugRequest("VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        Drug drug = Drug.from(request);
        when(drugService.addNewDrug(any())).thenReturn(drug);

        // then
        mockMvc.perform(post("/drug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
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
    void shouldReturnErrorWhenParamsAreInvalid(String name) throws Exception {
        mockMvc.perform(post("/drug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(name))
                .andExpect(status().isBadRequest());
    }
}