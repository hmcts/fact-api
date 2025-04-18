package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
class AdminCourtAreaOfLawEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String AREAS_OF_LAW_PATH = "/courtAreasOfLaw";
    private static final String AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG = "aylesbury-county-court-and-family-court";
    private static final String AYLESBURY_COURT_AREAS_OF_LAW_PATH = ADMIN_COURTS_ENDPOINT
        + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + AREAS_OF_LAW_PATH;
    private static final String AYLESBURY_COURT_AREAS_OF_LAW_NOT_FOUND_PATH = ADMIN_COURTS_ENDPOINT + "NotFound" + AREAS_OF_LAW_PATH;
    private static final String TEST_AREA_OF_LAW_NAME = "Employment";
    private static final int TEST_AREA_OF_LAW_ID = 34_260;

    /************************************************************* GET request tests section. ***************************************************************/
    @Test
    void returnAreaOfLawForTheCourt() {
        final var response = doGetRequest(
            AYLESBURY_COURT_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AreaOfLaw> areasOfLaw = response.body().jsonPath().getList(".", AreaOfLaw.class);
        assertThat(areasOfLaw).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingAreaOfLawForTheCourt() {
        final var response = doGetRequest(AYLESBURY_COURT_AREAS_OF_LAW_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAreaOfLawForTheCourt() {
        final var response = doGetRequest(
            AYLESBURY_COURT_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldReturnNotFoundWhenCourtDoesNotExist() {
        final Response response = doGetRequest(
            AYLESBURY_COURT_AREAS_OF_LAW_NOT_FOUND_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* PUT request tests section. ***************************************************************/
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Test
    void shouldUpdateCourtAreaOfLaw() throws JsonProcessingException {
        final List<AreaOfLaw> currentCourtAreasOfLaw = getCurrentCourtAreasOfLaw()
            .stream()
            .map(aol -> {
                AreaOfLaw newAol = new AreaOfLaw();
                newAol.setId(aol.getId());
                newAol.setName(aol.getName());
                return newAol;
            })
            .collect(Collectors.toList());
        final List<AreaOfLaw> expectedCourtAreasOfLaw = updateCourtAreasOfLaw(currentCourtAreasOfLaw);
        final String updatedJson = objectMapper().writeValueAsString(expectedCourtAreasOfLaw);
        final String originalJson = objectMapper().writeValueAsString(currentCourtAreasOfLaw);

        final var response = doPutRequest(
            AYLESBURY_COURT_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AreaOfLaw> updatedCourtAreaOfLaw = response.body().jsonPath().getList(
            ".",
            AreaOfLaw.class
        );
        assertThat(updatedCourtAreaOfLaw).containsExactlyElementsOf(expectedCourtAreasOfLaw);

        //clean up by removing added record
        final var cleanUpResponse = doPutRequest(
            AYLESBURY_COURT_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final List<AreaOfLaw> cleanCourtAreaOfLaw = cleanUpResponse.body().jsonPath().getList(
            ".",
            AreaOfLaw.class
        );
        assertThat(cleanCourtAreaOfLaw).containsExactlyElementsOf(currentCourtAreasOfLaw);
    }

    @Test
    void shouldRequireATokenWhenUpdatingAreaOfLawForTheCourt() throws JsonProcessingException {
        final var response = doPutRequest(AYLESBURY_COURT_AREAS_OF_LAW_PATH, getTestAreasOfLawJson());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForUpdatingAreaOfLawForTheCourt() throws JsonProcessingException {
        final var response = doPutRequest(
            AYLESBURY_COURT_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), getTestAreasOfLawJson()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldnotUpdateAndReturnNotFoundWhenCourtDoesNotExist() throws JsonProcessingException {
        final Response response = doPutRequest(
            AYLESBURY_COURT_AREAS_OF_LAW_NOT_FOUND_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            getTestAreasOfLawJson()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/
    private List<AreaOfLaw> getCurrentCourtAreasOfLaw() {
        final var response = doGetRequest(
            AYLESBURY_COURT_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.body().jsonPath().getList(".", AreaOfLaw.class);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<AreaOfLaw> updateCourtAreasOfLaw(final List<AreaOfLaw> courtAreaOfLaw) {
        final AreaOfLaw areaOfLaw = new AreaOfLaw();
        areaOfLaw.setId(TEST_AREA_OF_LAW_ID);
        areaOfLaw.setName(TEST_AREA_OF_LAW_NAME);
        areaOfLaw.setSinglePointEntry(false);
        final List<AreaOfLaw> updatedAreaOfLaw = new ArrayList<>(courtAreaOfLaw);
        updatedAreaOfLaw.add(areaOfLaw);
        return updatedAreaOfLaw;
    }

    private static String getTestAreasOfLawJson() throws JsonProcessingException {
        AreaOfLaw areaOfLaw = new AreaOfLaw();
        areaOfLaw.setId(1);
        areaOfLaw.setName("test1");
        areaOfLaw.setSinglePointEntry(false);
        AreaOfLaw areaOfLaw2 = new AreaOfLaw();
        areaOfLaw2.setId(2);
        areaOfLaw2.setName("test2");
        areaOfLaw2.setSinglePointEntry(false);
        final List<AreaOfLaw> courtAreaOfLaw = Arrays.asList(
            areaOfLaw,
            areaOfLaw2
        );
        return objectMapper().writeValueAsString(courtAreaOfLaw);
    }
}
