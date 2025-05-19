package com.pharma.homework.repository;

import com.pharma.homework.model.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {

    @Modifying
    @Query("UPDATE Drug d SET d.stock = d.stock - :quantity WHERE d.id = :drugId AND d.stock >= :quantity")
    int decreaseStock(@Param("drugId") Long drugId, @Param("quantity") Integer quantity);

}
