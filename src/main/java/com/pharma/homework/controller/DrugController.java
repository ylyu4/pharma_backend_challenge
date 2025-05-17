package com.pharma.homework.controller;

import com.pharma.homework.dto.NewDrugRequest;
import com.pharma.homework.model.Drug;
import com.pharma.homework.service.DrugService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @PostMapping("/drug")
    public ResponseEntity<Drug> addDrug(@Valid @RequestBody NewDrugRequest newDrugRequest) {
        Drug savedDrug = drugService.addNewDrug(newDrugRequest);
        return ResponseEntity.ok(savedDrug);
    }
}
