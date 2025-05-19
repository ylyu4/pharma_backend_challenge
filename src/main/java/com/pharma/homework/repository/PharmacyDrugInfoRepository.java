package com.pharma.homework.repository;

import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacyDrugInfoRepository extends JpaRepository<PharmacyDrugInfo, Long> {

    Optional<PharmacyDrugInfo> findByPharmacyAndDrug(Pharmacy pharmacy, Drug drug);
}
