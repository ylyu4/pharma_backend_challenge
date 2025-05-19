package com.pharma.homework.repository;

import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PharmacyDrugInfoRepository extends JpaRepository<PharmacyDrugInfo, Long> {

    Optional<PharmacyDrugInfo> findByPharmacyAndDrug(Pharmacy pharmacy, Drug drug);

    @Modifying
    @Query("UPDATE PharmacyDrugInfo p SET p.dispensingAmount = p.dispensingAmount + :quantity "
            + "WHERE p.pharmacy.id = :pharmacyId AND p.drug.id = :drugId AND (p.dispensingAmount + :quantity) <= p.maxAllocationAmount")
    int increaseDispensingAmount(@Param("pharmacyId") Long pharmacyId,
                                 @Param("drugId") Long drugId,
                                 @Param("quantity") Integer quantity);

}
