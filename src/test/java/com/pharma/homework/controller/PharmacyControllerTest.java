package com.pharma.homework.controller;

import com.pharma.homework.TestUtils;
import com.pharma.homework.dto.response.DrugInfoResponse;
import com.pharma.homework.dto.response.PharmaciesResponse;
import com.pharma.homework.model.Drug;
import com.pharma.homework.model.Pharmacy;
import com.pharma.homework.model.PharmacyDrugInfo;
import com.pharma.homework.service.PharmacyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PharmacyController.class)
public class PharmacyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PharmacyService pharmacyService;

    @Test
    void should_return_all_pharmacies_info() throws Exception {
        // given
        Drug drug = TestUtils.generateDrug(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 100);
        Pharmacy pharmacy1 = new Pharmacy(1L, "Pharmacy1", "Address1", "Phone1");
        PharmacyDrugInfo drugInfo = TestUtils.generateDrugInfo(pharmacy1, drug, 50, 200);
        pharmacy1.setDrugInfoSet(Set.of(drugInfo));
        Pharmacy pharmacy2 = new Pharmacy(2L, "Pharmacy2", "Address2", "Phone2");
        pharmacy2.setDrugInfoSet(Set.of(drugInfo));
        PharmaciesResponse response1 = new PharmaciesResponse(1L, "Pharmacy1", "Address1", "Phone1",
                List.of(new DrugInfoResponse(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 200, 50)));
        PharmaciesResponse response2 = new PharmaciesResponse(2L, "Pharmacy2", "Address2", "Phone2",
                List.of(new DrugInfoResponse(1L, "VitaminB", "Unknown", "x123456", LocalDate.now(), 200, 50)));

        when(pharmacyService.getAllPharmacyDrugInfo()).thenReturn(List.of(response1, response2));

        // then
        mockMvc.perform(get("/pharmacies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pharmacy1"))
                .andExpect(jsonPath("$[1].name").value("Pharmacy2"))
                .andExpect(jsonPath("$[0].drugInfoList[0].maxAllocationAmount").value(200))
                .andExpect(jsonPath("$[1].drugInfoList[0].dispensingAmount").value(50));
    }

}
