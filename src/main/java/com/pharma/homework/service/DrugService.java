package com.pharma.homework.service;

import com.pharma.homework.dto.NewDrugRequest;
import com.pharma.homework.model.Drug;
import com.pharma.homework.repository.DrugRepository;
import org.springframework.stereotype.Service;

@Service
public class DrugService {

    private final DrugRepository drugRepository;

    public DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    public Drug addNewDrug(NewDrugRequest request) {
        return drugRepository.save(Drug.from(request));
    }
}
