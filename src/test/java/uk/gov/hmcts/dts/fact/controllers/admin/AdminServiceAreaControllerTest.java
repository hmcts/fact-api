package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.services.ServiceAreaService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminServiceAreaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminServiceAreaControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private ServiceAreaService serviceAreaService;

    @Test
    void shouldGetAllServiceAreas() throws Exception {

        List<ServiceArea> serviceAreaList = new ArrayList<>();
        ServiceArea serviceArea = new ServiceArea();
        serviceArea.setAreaOfLawName("area of law");
        serviceArea.setDescription("description");
        serviceAreaList.add(serviceArea);
        when(serviceAreaService.getAllServiceAreas()).thenReturn(serviceAreaList);
        mockMvc.perform(get("/admin/serviceAreas"))
            .andExpect(status().isOk())
            .andExpect(content().json(new ObjectMapper().writeValueAsString(serviceAreaList)));
    }

}
