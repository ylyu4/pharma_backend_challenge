package com.pharma.homework.dto.response;

import java.util.List;

public class PharmaciesResponse {

    private Long pharmaciesId;

    private String name;

    private String address;

    private String phone;

    private List<DrugInfoResponse> drugInfoList;

    public Long getPharmaciesId() {
        return pharmaciesId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public List<DrugInfoResponse> getDrugInfoList() {
        return drugInfoList;
    }

    public PharmaciesResponse(Long pharmaciesId,
                              String name,
                              String address,
                              String phone,
                              List<DrugInfoResponse> drugInfoList) {
        this.address = address;
        this.name = name;
        this.pharmaciesId = pharmaciesId;
        this.phone = phone;
        this.drugInfoList = drugInfoList;
    }
}
