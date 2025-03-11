package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.model.admin.CourtSecondaryAddressType;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAddressService;
import uk.gov.hmcts.dts.fact.util.MvcSecurityUtil;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@SuppressWarnings("PMD.ExcessiveImports")
@WebMvcTest(AdminCourtAddressController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtAddressControllerTest {
    private static final String TEST_SLUG = "court-slug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String NOT_FOUND = "Not found: ";
    private static final String BASE_PATH = "/admin/courts/";
    private static final String ADDRESSES_PATH = "/" + "addresses";
    private static final List<String> ADDRESS1 = Arrays.asList("first address line 1", "first address line 2");
    private static final List<String> ADDRESS2 = Arrays.asList("second address line 1", "second address line 2");
    private static final String TOWN_NAME1 = "first town";
    private static final String TOWN_NAME2 = "second town";
    private static final String POSTCODE1 = "first postcode";
    private static final String POSTCODE2 = "second postcode";
    private static final Integer COUNTY = 1;
    private static final Integer SORT_ORDER = 0;
    private static final String EPIM_ID = "epim-id";
    private static final String EPIM_ID2 = "TEST-EPIMY1!!!!";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final CourtSecondaryAddressType COURT_SECONDARY_ADDRESS_TYPE_LIST = new CourtSecondaryAddressType(
        Arrays.asList(
            new AreaOfLaw(
                new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
                    34_257, "Civil partnership"), false),
            new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
                34_248, "Adoption"), false)
        ),
        Arrays.asList(
            new CourtType(
                new uk.gov.hmcts.dts.fact.entity.CourtType(11_417, "Family Court", "Family")
            ),
            new CourtType(
                new uk.gov.hmcts.dts.fact.entity.CourtType(11_418, "Tribunal", "Tribunal")
            )
        )
    );

    private static final List<CourtAddress> COURT_ADDRESSES = Arrays.asList(
        new CourtAddress(1, 1, ADDRESS1, null, TOWN_NAME1, null, COUNTY, POSTCODE1, COURT_SECONDARY_ADDRESS_TYPE_LIST, SORT_ORDER, EPIM_ID),
        new CourtAddress(2, 1, ADDRESS2, null, TOWN_NAME2, null, COUNTY, POSTCODE2, COURT_SECONDARY_ADDRESS_TYPE_LIST, SORT_ORDER, EPIM_ID)
    );

    private static final List<CourtAddress> COURT_ADDRESSES_WITH_BAD_EPIM = Arrays.asList(
        new CourtAddress(1, 1, ADDRESS1, null, TOWN_NAME1, null, COUNTY, POSTCODE1, COURT_SECONDARY_ADDRESS_TYPE_LIST, SORT_ORDER, EPIM_ID),
        new CourtAddress(2, 1, ADDRESS2, null, TOWN_NAME2, null, COUNTY, POSTCODE2, COURT_SECONDARY_ADDRESS_TYPE_LIST, SORT_ORDER, EPIM_ID2)
    );
    private static final String TEST_ADDRESSES_FILE = "courtAddresses.json";

    private static String courtAddressesJson;

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtAddressService adminService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        courtAddressesJson = OBJECT_MAPPER.writeValueAsString(COURT_ADDRESSES);
    }

    @Test
    void shouldRetrieveCourtAddresses() throws Exception {
        when(adminService.getCourtAddressesBySlug(TEST_SLUG)).thenReturn(COURT_ADDRESSES);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ADDRESSES_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(courtAddressesJson));
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingAddressesForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtAddressesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ADDRESSES_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void shouldCallvalidateAndSaveAddresses() {
        adminService.validateAndSaveAddresses(COURT_ADDRESSES, TEST_SLUG, null);
        verify(adminService).validateAndSaveAddresses(COURT_ADDRESSES, TEST_SLUG, null);
    }

    @Test
    void shouldCallvalidateAndSaveAddressesWithNoAddressesPassedIn() {
        adminService.validateAndSaveAddresses(emptyList(), TEST_SLUG, null);
        verify(adminService).validateAndSaveAddresses(emptyList(), TEST_SLUG, null);
    }

    @Test
    void updateAddressShouldReturnUpdatedAddress() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_ADDRESSES_FILE);
        final List<CourtAddress> courtAddresses = asList(OBJECT_MAPPER.readValue(expectedJson, CourtAddress[].class));

        when(adminService.validateAndSaveAddresses(COURT_ADDRESSES, TEST_SLUG, null))
            .thenReturn(ResponseEntity.ok(courtAddresses));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ADDRESSES_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
