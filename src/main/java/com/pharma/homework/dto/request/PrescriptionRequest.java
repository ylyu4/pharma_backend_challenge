package com.pharma.homework.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PrescriptionRequest(
        @NotNull(message = "patientId can not be null")
        Long patientId,

        @NotNull(message = "pharmacyId can not be null")
        Long pharmacyId,

        @Valid
        @NotEmpty(message = "drug list can not be empty")
        List<@Valid DrugRequestInfo> drugs
) {
    public record DrugRequestInfo(
            @NotNull(message = "drug ID can not be null")
            Long drugId,

            @NotNull(message = "quantity can not be null")
            @Min(value = 1, message = "quantity must be at least 1")
            Integer quantity
    ) {}
}
