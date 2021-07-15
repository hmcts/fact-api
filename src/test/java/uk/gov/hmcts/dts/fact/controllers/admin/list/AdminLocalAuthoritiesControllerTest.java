package uk.gov.hmcts.dts.fact.controllers.admin.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminLocalAuthorityService;
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
    private static final String BIRMINGHAM_CITY_COUNCIL = "Birmingham City Council";

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
        final LocalAuthority responseLocalAuthority = new LocalAuthority(1234, BIRMINGHAM_CITY_COUNCIL);
        when(localAuthorityService.updateLocalAuthority(eq(1234), anyString())).thenReturn(responseLocalAuthority);
        when(validationService.validateLocalAuthority(any())).thenReturn(true);

        final String expectedResponseJson = new ObjectMapper().writeValueAsString(responseLocalAuthority);

        mockMvc.perform(put(BASE_PATH + "/1234")
                            .content(BIRMINGHAM_CITY_COUNCIL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponseJson));

        verify(localAuthorityService).updateLocalAuthority(1234, BIRMINGHAM_CITY_COUNCIL);
    }

    @Test
    void updateShouldReturnNotFoundIfLocalAuthorityDoesNotExist() throws Exception {
        when(localAuthorityService.updateLocalAuthority(eq(321), anyString()))
            .thenThrow(new NotFoundException("Local Authority Not Found"));
        when(validationService.validateLocalAuthority(any())).thenReturn(true);

        mockMvc.perform(put(BASE_PATH + "/321")
                            .content(BIRMINGHAM_CITY_COUNCIL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnBadRequestIfLocalAuthorityNameInvalid() throws Exception {
        when(localAuthorityService.updateLocalAuthority(eq(321), anyString()))
            .thenReturn(new LocalAuthority(1234, BIRMINGHAM_CITY_COUNCIL));
        when(validationService.validateLocalAuthority(any())).thenReturn(false);

        mockMvc.perform(put(BASE_PATH + "/321")
                            .content("Brimgham Council")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldReturnConflictIfLocalAuthorityAlreadyExists() throws Exception {
        when(localAuthorityService.updateLocalAuthority(eq(321), anyString()))
            .thenThrow(new DuplicatedListItemException("Local authority already exists."));
        when(validationService.validateLocalAuthority(any())).thenReturn(true);

        mockMvc.perform(put(BASE_PATH + "/321")
                            .content(BIRMINGHAM_CITY_COUNCIL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }
}
