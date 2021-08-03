package uk.gov.hmcts.dts.fact.controllers.admin.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminFacilityService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminFacilitiesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminFacilitiesControllerTest {

    private static final String BASE_PATH = "/admin/facilities";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminFacilityService adminFacilityService;

    @Test
    void shouldReturnAllFacilities() throws Exception {

        final uk.gov.hmcts.dts.fact.entity.FacilityType facilityType1 = new uk.gov.hmcts.dts.fact.entity.FacilityType();
        facilityType1.setId(1);
        facilityType1.setName("FacilityType1");
        facilityType1.setOrder(1);
        final uk.gov.hmcts.dts.fact.entity.FacilityType facilityType2 = new uk.gov.hmcts.dts.fact.entity.FacilityType();
        facilityType2.setId(2);
        facilityType2.setName("FacilityType2");
        facilityType2.setOrder(2);
        final uk.gov.hmcts.dts.fact.entity.FacilityType facilityType3 = new uk.gov.hmcts.dts.fact.entity.FacilityType();
        facilityType3.setId(3);
        facilityType3.setName("FacilityType3");
        facilityType3.setOrder(3);


        final List<FacilityType> mockFacilityTypes = Arrays.asList(
            new FacilityType(facilityType1),
            new FacilityType(facilityType2),
            new FacilityType(facilityType3)
        );

        when(adminFacilityService.getAllFacilityTypes()).thenReturn(mockFacilityTypes);

        final String allFacilityTypesJson = new ObjectMapper().writeValueAsString(mockFacilityTypes);

        mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(allFacilityTypesJson));
    }

}
