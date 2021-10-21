package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
public class AdminCourtFacilityEndpointTest extends AdminFunctionalTestBase {

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
    public void returnFacilitiesForTheCourt() {
        final var response = doGetRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<Facility> facilities = response.body().jsonPath().getList(".", Facility.class);
        assertThat(facilities).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingFacilitiesForTheCourt() {
        final var response = doGetRequest(AYLESBURY_COURT_FACILITIES_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingFacilitiesForTheCourt() {
        final var response = doGetRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldNotRetrieveFacilitiesWhenCourtSlugNotFound() {
        final var response = doGetRequest(COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* PUT request tests section. ***************************************************************/
    @Test
    public void shouldUpdateCourtFacilities() throws JsonProcessingException {
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
        assertThat(updatedFacilities).containsExactlyElementsOf(getExpectedFacilities(facilitiesToBeUpdated));

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
    public void shouldRequireATokenWhenUpdatingFacilitiesForTheCourt() throws JsonProcessingException {
        final var response = doPutRequest(AYLESBURY_COURT_FACILITIES_PATH, getTestFacilities());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingFacilitiesForTheCourt() throws JsonProcessingException {
        final var response = doPutRequest(
            AYLESBURY_COURT_FACILITIES_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), getTestFacilities()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldNotUpdateFacilitiesWhenCourtSlugNotFound() throws JsonProcessingException {
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

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<Facility> getExpectedFacilities(final List<Facility> facilities) {
        final Map<Integer, FacilityType> facilityTypeMap = getFacilityTypes().stream()
            .collect(toMap(FacilityType::getId, type -> type));
        // Sort the facilities by the facility type sort order
        return facilities.stream()
            .sorted(Comparator.comparingInt(f -> facilityTypeMap.get(f.getId()).getOrder()))
            .collect(toList());
    }

    private List<FacilityType> getFacilityTypes() {
        final Response response = doGetRequest(ADMIN_FACILITY_TYPES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        return response.body().jsonPath().getList(".", FacilityType.class);
    }
}
