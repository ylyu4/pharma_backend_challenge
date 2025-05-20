package com.pharma.homework.repository;

import com.pharma.homework.model.AuditLog;
import com.pharma.homework.model.AuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(""" 
            SELECT a FROM AuditLog a
            WHERE (:patientId IS NULL OR a.patientId = :patientId)
            AND (:pharmacyId IS NULL OR a.pharmacyId = :pharmacyId)
            AND (:status IS NULL OR a.status = :status)
        """)
    List<AuditLog> findByConditions(@Param("patientId") Long patientId,
                                    @Param("pharmacyId") Long pharmacyId,
                                    @Param("status") AuditStatus status);
}
