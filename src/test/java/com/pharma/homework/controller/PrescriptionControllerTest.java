package com.pharma.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharma.homework.dto.request.PrescriptionRequest;
import com.pharma.homework.dto.response.PrescriptionStatusResponse;
import com.pharma.homework.model.PrescriptionStatus;
import com.pharma.homework.service.PrescriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrescriptionController.class)
class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrescriptionService prescriptionService;

    @Test
    void should_create_prescription_successfully() throws Exception {
        // given
        PrescriptionRequest request = new PrescriptionRequest(
                123L,
                1L,
                List.of(new PrescriptionRequest.DrugRequestInfo(100L, 2))
        );

        PrescriptionStatusResponse mockResponse = new PrescriptionStatusResponse(999L, PrescriptionStatus.CREATED);

        when(prescriptionService.save(any())).thenReturn(mockResponse);

        // then
        mockMvc.perform(post("/prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prescriptionId").value(999L))
                .andExpect(jsonPath("$.prescriptionStatus").value("CREATED"));
    }

    @Test
    void should_return_bad_request_when_missing_patient_id() throws Exception {
        // given
        String invalidJson = """
        {
            "pharmacyId": 1,
            "drugs": [{
                "drugId": 100,
                "quantity": 2
            }]
        }
        """;

        // then
        mockMvc.perform(post("/prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.patientId").value("patientId can not be null"));
    }

    @Test
    void should_return_bad_request_when_invalid_quantity() throws Exception {
        // given
        PrescriptionRequest request = new PrescriptionRequest(
                123L,
                1L,
                List.of(new PrescriptionRequest.DrugRequestInfo(100L, 0))
        );

        // then
        mockMvc.perform(post("/prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['drugs[0].quantity']").value("quantity must be at least 1"));
    }

    @Test
    void should_return_bad_request_when_empty_drug_list() throws Exception {
        // given
        PrescriptionRequest request = new PrescriptionRequest(
                123L,
                1L,
                Collections.emptyList()
        );

        // then
        mockMvc.perform(post("/prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.drugs").value("drug list can not be empty"));
    }

    @Test
    void should_return_bad_request_when_missing_drug_id() throws Exception {
        // given
        String invalidJson = """
        {
            "patientId": 123,
            "pharmacyId": 1,
            "drugs": [{
                "quantity": 2
            }]
        }
        """;

        // then
        mockMvc.perform(post("/prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['drugs[0].drugId']").value("drug ID can not be null"));
    }

    @Test
    void should_return_bad_request_when_pharmacyId_null() throws Exception {
        // given
        PrescriptionRequest request = new PrescriptionRequest(
                123L,
                null,
                List.of(new PrescriptionRequest.DrugRequestInfo(100L, 2))
        );

        // then
        mockMvc.perform(post("/prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.pharmacyId").value("pharmacyId can not be null"));
    }
}