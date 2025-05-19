package com.pharma.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharma.homework.dto.request.PrescriptionRequest;
import com.pharma.homework.dto.response.PrescriptionStatusResponse;
import com.pharma.homework.exception.DrugExpireException;
import com.pharma.homework.exception.InvalidPrescriptionStatusException;
import com.pharma.homework.exception.PrescriptionNotFoundException;
import com.pharma.homework.model.PrescriptionStatus;
import com.pharma.homework.service.PrescriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
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

    @Test
    void test_fulfill_prescription_successfully() throws Exception {
        // given
        Long prescriptionId = 1L;
        PrescriptionStatusResponse mockResponse = new PrescriptionStatusResponse(
                prescriptionId,
                PrescriptionStatus.FULFILLED
        );
        when(prescriptionService.fulfillPrescription(prescriptionId))
                .thenReturn(mockResponse);

        // then
        mockMvc.perform(post("/prescription/{id}/fulfill", prescriptionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prescriptionId").value(prescriptionId))
                .andExpect(jsonPath("$.prescriptionStatus").value("FULFILLED"));
    }

    @Test
    void test_fulfill_prescription_not_found() throws Exception {
        // given
        Long invalidId = 999L;
        when(prescriptionService.fulfillPrescription(invalidId))
                .thenThrow(new PrescriptionNotFoundException(invalidId));

        // then
        mockMvc.perform(post("/prescription/{id}/fulfill", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_fulfill_prescription_invalid_status() throws Exception {
        // given
        Long prescriptionId = 2L;
        PrescriptionStatus currentStatus = PrescriptionStatus.FULFILLED;
        when(prescriptionService.fulfillPrescription(prescriptionId))
                .thenThrow(new InvalidPrescriptionStatusException(prescriptionId, currentStatus));

        mockMvc.perform(post("/prescription/{id}/fulfill", prescriptionId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_fulfill_prescription_concurrent_conflict() throws Exception {
        Long prescriptionId = 3L;

        when(prescriptionService.fulfillPrescription(prescriptionId))
                .thenThrow(new OptimisticLockingFailureException("Concurrent modification occurred"));

        mockMvc.perform(post("/prescription/{id}/fulfill", prescriptionId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("IS_CONFLICT"));
    }

    @Test
    void test_fulfill_prescription_drug_expired() throws Exception {
        Long prescriptionId = 4L;
        Long drugId = 100L;

        when(prescriptionService.fulfillPrescription(prescriptionId))
                .thenThrow(new DrugExpireException(drugId));

        mockMvc.perform(post("/prescription/{id}/fulfill", prescriptionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }
}