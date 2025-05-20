package com.pharma.homework.controller;

import com.pharma.homework.dto.request.CreateDrugRequest;
import com.pharma.homework.dto.request.DrugAddRequest;
import com.pharma.homework.dto.response.DrugResponse;
import com.pharma.homework.service.DrugService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drugs")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @PostMapping("")
    public ResponseEntity<DrugResponse> createDrug(@Valid @RequestBody CreateDrugRequest createDrugRequest) {
        DrugResponse drugResponse = drugService.createNewDrug(createDrugRequest);
        return ResponseEntity.ok(drugResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrugResponse> updateDrug(@PathVariable Long id, @Valid @RequestBody DrugAddRequest drugAddRequest) {
        DrugResponse drugResponse = drugService.addDrug(id, drugAddRequest);
        return ResponseEntity.ok(drugResponse); // OK
    }
}
