package com.pharma.homework.repository;

import com.pharma.homework.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    @Query("SELECT DISTINCT p FROM Pharmacy p LEFT JOIN FETCH p.drugInfoSet s LEFT JOIN FETCH s.drug")
    List<Pharmacy> findAllWithDrugInfo();
}
