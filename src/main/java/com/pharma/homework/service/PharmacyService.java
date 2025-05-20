package com.pharma.homework.service;

import com.pharma.homework.dto.response.PharmaciesResponse;

import com.pharma.homework.mapper.PharmacyDrugInfoMapper;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.repository.PharmacyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    public PharmacyService(PharmacyRepository pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }

    @Transactional(readOnly = true)
    public List<PharmaciesResponse> getAllPharmacyDrugInfo() {
        List<Pharmacy> pharmacies = pharmacyRepository.findAllWithDrugInfo();
        return pharmacies.stream().map(PharmacyDrugInfoMapper::toResponse).toList();
    }
}
