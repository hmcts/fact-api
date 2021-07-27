package uk.gov.hmcts.dts.fact.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAddressService;

@WebMvcTest(AdminCourtAddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtAddressControllerTest {
    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtAddressService adminService;

}
