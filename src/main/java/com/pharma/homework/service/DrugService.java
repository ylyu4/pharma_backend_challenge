package com.pharma.homework.service;

import com.pharma.homework.dto.CreateDrugRequest;
import com.pharma.homework.dto.DrugAddRequest;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.model.Drug;
import com.pharma.homework.repository.DrugRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DrugService {

    private final DrugRepository drugRepository;

    public DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    public Drug createNewDrug(CreateDrugRequest request) {
        return drugRepository.save(Drug.from(request));
    }

    public Drug addDrug(DrugAddRequest request) {
        Optional<Drug> optionalDrug = drugRepository.findDrugById(request.getId()).stream().findFirst();
        if (optionalDrug.isEmpty()) {
            throw new DrugNotFoundException(request.getId());
        }
        Drug updatedDrug = optionalDrug.get();
        updatedDrug.addStock(request.getAddedStock());
        return drugRepository.save(updatedDrug);
    }
}
