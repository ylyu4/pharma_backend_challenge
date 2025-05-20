package com.pharma.homework.dto.response;

import com.pharma.homework.model.PrescriptionStatus;

public record PrescriptionStatusResponse(Long prescriptionId, PrescriptionStatus prescriptionStatus) {

}
