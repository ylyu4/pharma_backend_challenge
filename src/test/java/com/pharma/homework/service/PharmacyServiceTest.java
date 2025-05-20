package com.pharma.homework.service;


import com.pharma.homework.TestUtils;
import com.pharma.homework.dto.response.DrugInfoResponse;
import com.pharma.homework.dto.response.PharmaciesResponse;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import com.pharma.homework.repository.PharmacyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PharmacyServiceTest {

    @Mock
    private PharmacyRepository pharmacyRepository;


    @InjectMocks
    private PharmacyService pharmacyService;


    @Test
    void test_get_all_pharmacies() {
        // given
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        Pharmacy pharmacy1 = new Pharmacy(1L, "Pharmacy1", "Address1", "Phone1");
        PharmacyDrugInfo drugInfo = TestUtils.generateDrugInfo(pharmacy1, drug, 50, 200);
        pharmacy1.setDrugInfoSet(Set.of(drugInfo));
        Pharmacy pharmacy2 = new Pharmacy(2L, "Pharmacy2", "Address2", "Phone2");
        pharmacy2.setDrugInfoSet(Set.of(drugInfo));
        List<Pharmacy> pharmacies = List.of(pharmacy1, pharmacy2);
        PharmaciesResponse response1 = new PharmaciesResponse(1L, "Pharmacy1", "Address1", "Phone1",
                List.of(new DrugInfoResponse(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 200, 50)));
        PharmaciesResponse response2 = new PharmaciesResponse(2L, "Pharmacy2", "Address2", "Phone2",
                List.of(new DrugInfoResponse(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 200, 50)));

        when(pharmacyRepository.findAllWithDrugInfo()).thenReturn(pharmacies);

        // when
        List<PharmaciesResponse> result = pharmacyService.getAllPharmacyDrugInfo();

        // then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).pharmaciesId());
        assertEquals(2L, result.get(1).pharmaciesId());
        assertEquals(50, result.get(1).drugInfoList().get(0).dispensingAmount());
        assertEquals(200, result.get(1).drugInfoList().get(0).maxAllocationAmount());
    }
}
