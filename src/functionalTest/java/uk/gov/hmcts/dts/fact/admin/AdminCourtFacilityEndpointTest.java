package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.html.sanitizer.OwaspHtmlSanitizer;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
@SuppressWarnings("PMD.TooManyMethods")
class AdminCourtFacilityEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String ADMIN_FACILITY_TYPES_ENDPOINT = "/admin/facilities";
    private static final String FACILITIES_PATH = "/facilities";
    private static final String AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG = "aylesbury-county-court-and-family-court";
    private static final String GREENWICH_MAGISTRATES_COURT_SLUG = "greenwich-magistrate-court";

    private static final String AYLESBURY_COURT_FACILITIES_PATH = ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG
        + FACILITIES_PATH;
    private static final String COURT_NOT_FIND_PATH = ADMIN_COURTS_ENDPOINT + GREENWICH_MAGISTRATES_COURT_SLUG
        + FACILITIES_PATH;

    private static final Integer TEST_FACILITY_ID_1 = 45;
    private static final Integer TEST_FACILITY_ID_2 = 50;
    private static final String TEST_FACILITY_DESCRIPTION = "TEST";
    private static final String TEST_FACILITY_DESCRIPTION_CY = "TESTCY";


    /************************************************************* GET request tests section. ***************************************************************/
    @Test
    void returnFacilitiesForTheCourt() {
        final var response = doGetRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<Facility> facilities = response.body().jsonPath().getList(".", Facility.class);
        assertThat(facilities).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingFacilitiesForTheCourt() {
        final var response = doGetRequest(AYLESBURY_COURT_FACILITIES_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingFacilitiesForTheCourt() {
        final var response = doGetRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldNotRetrieveFacilitiesWhenCourtSlugNotFound() {
        final var response = doGetRequest(COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* PUT request tests section. ***************************************************************/
    @Test
    void shouldUpdateCourtFacilities() throws JsonProcessingException {
        final List<Facility> currentCourtFacilities = getCurrentFacilities();
        final List<Facility> facilitiesToBeUpdated = updateFacilities(currentCourtFacilities);
        final String updatedJson = objectMapper().writeValueAsString(facilitiesToBeUpdated);
        final String originalJson = objectMapper().writeValueAsString(currentCourtFacilities);

        final var response = doPutRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<Facility> updatedFacilities = response.body().jsonPath().getList(
            ".",
            Facility.class
        );
        assertThat(updatedFacilities).containsExactlyElementsOf(facilitiesToBeUpdated);

        //clean up by removing added record
        final var cleanUpResponse = doPutRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final List<Facility> cleanFacilities = cleanUpResponse.body().jsonPath().getList(
            ".",
            Facility.class
        );
        assertThat(cleanFacilities).containsExactlyElementsOf(currentCourtFacilities);
    }

    @Test
    void shouldRequireATokenWhenUpdatingFacilitiesForTheCourt() throws JsonProcessingException {
        final var response = doPutRequest(AYLESBURY_COURT_FACILITIES_PATH, getTestFacilities());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForUpdatingFacilitiesForTheCourt() throws JsonProcessingException {
        final var response = doPutRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), getTestFacilities()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldNotUpdateFacilitiesWhenCourtSlugNotFound() throws JsonProcessingException {
        final var response = doPutRequest(
            COURT_NOT_FIND_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            getTestFacilities()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/
    private List<Facility> getCurrentFacilities() {
        final var response = doGetRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.body().jsonPath().getList(".", Facility.class);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<Facility> updateFacilities(final List<Facility> facilities) {
        for (Facility facility: facilities) {
            facility.setDescription(OwaspHtmlSanitizer.sanitizeHtml(facility.getDescription()));
            facility.setDescriptionCy(OwaspHtmlSanitizer.sanitizeHtml(facility.getDescriptionCy()));
        }
        final List<Facility> updatedFacilities = new ArrayList<>(facilities);
        Facility facility = new Facility();
        facility.setId(TEST_FACILITY_ID_1);
        facility.setDescription(TEST_FACILITY_DESCRIPTION);
        facility.setDescriptionCy(TEST_FACILITY_DESCRIPTION_CY);
        updatedFacilities.add(facility);
        return updatedFacilities;
    }

    private static String getTestFacilities() throws JsonProcessingException {
        final List<Facility> facilities = Arrays.asList(
            new Facility(TEST_FACILITY_ID_1, TEST_FACILITY_DESCRIPTION, TEST_FACILITY_DESCRIPTION_CY),
            new Facility(TEST_FACILITY_ID_2, TEST_FACILITY_DESCRIPTION, TEST_FACILITY_DESCRIPTION_CY)
        );
        return objectMapper().writeValueAsString(facilities);
    }
}
