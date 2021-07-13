package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.AdminLocalAuthorityService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminLocalAuthoritiesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminLocalAuthoritiesControllerTest {

    private static final String BASE_PATH = "/admin/localauthorities";
    private static final String GET_ALL_PATH = "/all";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminLocalAuthorityService localAuthorityService;

    @MockBean
    private ValidationService validationService;

    @Test
    void shouldReturnAllLocalAuthorities() throws Exception {

        final List<LocalAuthority> mockAllLocalAuthorities = Arrays.asList(
            new LocalAuthority(new uk.gov.hmcts.dts.fact.entity.LocalAuthority(100, "Manchester City Council")),
            new LocalAuthority(new uk.gov.hmcts.dts.fact.entity.LocalAuthority(200, "Doncaster Borough Council")),
            new LocalAuthority(new uk.gov.hmcts.dts.fact.entity.LocalAuthority(300, "Wolverhampton City Council")),
            new LocalAuthority(new uk.gov.hmcts.dts.fact.entity.LocalAuthority(400, "Hammersmith and Fulham Borough Council"))
            );
        when(localAuthorityService.getAllLocalAuthorities()).thenReturn(mockAllLocalAuthorities);

        final String allLocalAuthoritiesJson = new ObjectMapper().writeValueAsString(mockAllLocalAuthorities);

        mockMvc.perform(get(BASE_PATH + GET_ALL_PATH).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(allLocalAuthoritiesJson));
    }

    @Test
    void shouldUpdateLocalAuthorityWhenLocalAuthorityNameIsValid() throws Exception {
        final LocalAuthority localAuthority = new LocalAuthority(1234, "Birmingham City Council");

        when(localAuthorityService.updateLocalAuthority(eq(1234), any(LocalAuthority.class))).thenReturn(localAuthority);
        when(validationService.validateLocalAuthority(any())).thenReturn(true);

        final String requestJson = new ObjectMapper().writeValueAsString(localAuthority);

        mockMvc.perform(put(BASE_PATH + "/1234")
                            .content(requestJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(requestJson));

        verify(localAuthorityService, times(1))
            .updateLocalAuthority(1234, localAuthority);
    }

    @Test
    void updateShouldReturnNotFoundIfLocalAuthorityDoesNotExist() throws Exception {
        when(localAuthorityService.updateLocalAuthority(eq(321), any(LocalAuthority.class)))
            .thenThrow(new NotFoundException("Local Authority Not Found"));
        when(validationService.validateLocalAuthority(any())).thenReturn(true);

        final String requestJson = new ObjectMapper()
            .writeValueAsString(new LocalAuthority(321, "Birmingham City Council"));

        mockMvc.perform(put(BASE_PATH + "/321")
                            .content(requestJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnBadRequestIfLocalAuthorityNameInvalid() throws Exception {
        when(localAuthorityService.updateLocalAuthority(eq(321), any(LocalAuthority.class))).thenReturn(new LocalAuthority());
        when(validationService.validateLocalAuthority(any())).thenReturn(false);

        final String requestJson = new ObjectMapper()
            .writeValueAsString(new LocalAuthority(321, "Brimgham Council"));

        mockMvc.perform(put(BASE_PATH + "/321")
                            .content(requestJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}
