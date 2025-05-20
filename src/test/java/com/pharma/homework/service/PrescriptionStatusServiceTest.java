package com.pharma.homework.service;

import com.pharma.homework.model.Prescription;
import com.pharma.homework.model.PrescriptionStatus;
import com.pharma.homework.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrescriptionStatusServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private PrescriptionStatusService prescriptionStatusService;

    @Test
    void should_update_status_and_timestamp_and_save() {
        // given
        Prescription prescription = new Prescription();
        prescription.setStatus(PrescriptionStatus.CREATED);
        prescription.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Prescription savedPrescription = new Prescription();
        savedPrescription.setStatus(PrescriptionStatus.REJECTED);
        savedPrescription.setUpdatedAt(LocalDateTime.now());

        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(savedPrescription);

        // when
        Prescription result = prescriptionStatusService.storeRejectPrescription(prescription);

        // then
        assertNotNull(result);
        assertEquals(PrescriptionStatus.REJECTED, result.getStatus());
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(prescriptionRepository, times(1)).save(prescription);
    }
}