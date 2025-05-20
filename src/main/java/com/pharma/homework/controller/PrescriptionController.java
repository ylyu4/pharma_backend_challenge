package com.pharma.homework.controller;

import com.pharma.homework.dto.request.PrescriptionRequest;
import com.pharma.homework.dto.response.PrescriptionStatusResponse;
import com.pharma.homework.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<PrescriptionStatusResponse> createPrescription(@Valid @RequestBody PrescriptionRequest prescriptionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.save(prescriptionRequest));
    }

    @PutMapping("/{id}/status/fulfilled")
    public ResponseEntity<PrescriptionStatusResponse> fulfillPrescription(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.fulfillPrescription(id));
    }
}
