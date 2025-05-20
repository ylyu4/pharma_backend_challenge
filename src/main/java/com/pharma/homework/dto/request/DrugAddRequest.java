package com.pharma.homework.dto.request;

import jakarta.validation.constraints.NotNull;

public record DrugAddRequest(
    @NotNull(message = "Update stock can not be null")
    Integer addedStock) {
}
