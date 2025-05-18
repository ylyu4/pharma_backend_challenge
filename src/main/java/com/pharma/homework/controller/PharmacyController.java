package com.pharma.homework.controller;

import com.pharma.homework.dto.response.PharmaciesResponse;
import com.pharma.homework.service.PharmacyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pharmacy")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PharmaciesResponse>> getAllPharmaciesWithDrugInfo() {
        return ResponseEntity.ok(pharmacyService.getAllPharmacyDrugInfo());
    }

}
