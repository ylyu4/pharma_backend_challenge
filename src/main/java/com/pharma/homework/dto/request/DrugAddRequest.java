package com.pharma.homework.dto.request;

import jakarta.validation.constraints.NotNull;

public class DrugAddRequest {

    @NotNull(message = "Id can not be null")
    private Long id;

    @NotNull(message = "Update stock can not be null")
    private Integer addedStock;

    public Long getId() {
        return id;
    }

    public Integer getAddedStock() {
        return addedStock;
    }

    public DrugAddRequest(Long id, Integer addedStock) {
        this.id = id;
        this.addedStock = addedStock;
    }

}
