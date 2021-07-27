package uk.gov.hmcts.dts.fact.controllers.admin.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.controllers.admin.AdminCourtContactController;
import uk.gov.hmcts.dts.fact.model.admin.AddressType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtContactService;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAddressTypeService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAddressTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminAddressTypeControllerTest {
    private static final String PATH = "/admin/addressTypes";
    private static final List<AddressType> EXPECTED_ADDRESS_TYPES = Arrays.asList(
        new AddressType(1, "Visit us", "Visit us cy"),
        new AddressType(2, "Write to us", "Write to us cy")
    );
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminAddressTypeService adminService;

    @Test
    void shouldRetrieveAllAddressTypes() throws Exception {
        when(adminService.getAllAddressTypes()).thenReturn(EXPECTED_ADDRESS_TYPES);
        final String json  = OBJECT_MAPPER.writeValueAsString(EXPECTED_ADDRESS_TYPES);

        mockMvc.perform(get(PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(json));
    }
}
