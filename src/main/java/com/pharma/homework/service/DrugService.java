package com.pharma.homework.service;

import com.pharma.homework.dto.request.CreateDrugRequest;
import com.pharma.homework.dto.request.DrugAddRequest;
import com.pharma.homework.dto.response.DrugResponse;
import com.pharma.homework.exception.DrugNotFoundException;
import com.pharma.homework.mapper.DrugMapper;
import com.pharma.homework.model.Drug;
import com.pharma.homework.repository.DrugRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DrugService {

    private final DrugRepository drugRepository;

    private final DrugMapper drugMapper;

    public DrugService(DrugRepository drugRepository, DrugMapper drugMapper) {
        this.drugRepository = drugRepository;
        this.drugMapper = drugMapper;
    }

    public DrugResponse createNewDrug(CreateDrugRequest request) {
        Drug savedDrug = drugRepository.save(Drug.from(request));
        return drugMapper.toResponse(savedDrug);
    }

    public DrugResponse addDrug(DrugAddRequest request) {
        Optional<Drug> optionalDrug = drugRepository.findDrugById(request.getId()).stream().findFirst();
        if (optionalDrug.isEmpty()) {
            throw new DrugNotFoundException(request.getId());
        }
        Drug updatedDrug = optionalDrug.get();
        updatedDrug.addStock(request.getAddedStock());
        Drug savedDrug = drugRepository.save(updatedDrug);
        return drugMapper.toResponse(savedDrug);
    }
}
