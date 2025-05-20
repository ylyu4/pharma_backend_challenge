package com.pharma.homework.dto.response;

import java.util.List;

public record PharmaciesResponse(Long pharmaciesId, String name, String address, String phone, List<DrugInfoResponse> drugInfoList) {

}
