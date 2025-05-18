package com.pharma.homework.controller;

import com.pharma.homework.dto.request.CreateDrugRequest;
import com.pharma.homework.dto.request.DrugAddRequest;
import com.pharma.homework.dto.response.DrugResponse;
import com.pharma.homework.service.DrugService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drug")
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

    @PutMapping("/update")
    public ResponseEntity<DrugResponse> addDrug(@Valid @RequestBody DrugAddRequest drugAddRequest) {
        DrugResponse drugResponse = drugService.addDrug(drugAddRequest);
        return ResponseEntity.ok(drugResponse);
    }
}
