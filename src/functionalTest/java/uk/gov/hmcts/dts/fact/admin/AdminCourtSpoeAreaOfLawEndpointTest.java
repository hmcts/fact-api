package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.model.admin.SpoeAreaOfLaw;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
@SuppressWarnings("PMD.TooManyMethods")
public class AdminCourtSpoeAreaOfLawEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts";
    private static final String SPOE_AREAS_OF_LAW_PATH = "/SpoeAreasOfLaw";
    private static final String SPOE_AREA_OF_LAW_ENDPOINT = ADMIN_COURTS_ENDPOINT + SPOE_AREAS_OF_LAW_PATH;
    private static final String ABERYSTWYTH_JUSTICE_CENTRE_SLUG = "/aberystwyth-justice-centre";
    private static final String ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH = ADMIN_COURTS_ENDPOINT
        + ABERYSTWYTH_JUSTICE_CENTRE_SLUG + SPOE_AREAS_OF_LAW_PATH;
    private static final String ABERYSTWYTH_COURT_AREAS_OF_LAW_NOT_FOUND_PATH = ADMIN_COURTS_ENDPOINT + "NotFound" + SPOE_AREAS_OF_LAW_PATH;

    private static final String TEST_SPOE_AREA_OF_LAW_NAME = "Children";
    private static final int TEST_SPOE_AREA_OF_LAW_ID = 34_249;
    private static final Boolean TEST_SINGLE_POINT_OF_ENTRY = true;

    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void shouldReturnAllSpoeAreasOfLaw() {
        final Response response = doGetRequest(
            SPOE_AREA_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<SpoeAreaOfLaw> areaOfLaw = response.body().jsonPath().getList(".", SpoeAreaOfLaw.class);
        assertThat(areaOfLaw).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingSpoeAreasOfLaw() {
        final var response = doGetRequest(SPOE_AREA_OF_LAW_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingSpoeAreasOfLaw() {
        final var response = doGetRequest(
            SPOE_AREA_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* Spoe Get Request Tests for a court. ***************************************************************/

    @Test
    public void returnSpoeAreasOfLawForTheCourt() {
        final var response = doGetRequest(
            ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AreaOfLaw> areasOfLaw = response.body().jsonPath().getList(".", AreaOfLaw.class);
        assertThat(areasOfLaw).isNotEmpty();
    }

    @Test
    public void shouldRequireATokenWhenGettingSpoeAreasOfLawForTheCourt() {
        final var response = doGetRequest(ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingSpoeAreasOfLawForTheCourt() {
        final var response = doGetRequest(
            ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldReturnNotFoundWhenSpoeCourtDoesNotExist() {
        final Response response = doGetRequest(
            ABERYSTWYTH_COURT_AREAS_OF_LAW_NOT_FOUND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }
    /************************************************************* PUT request tests section. ***************************************************************/

    @Test
    public void shouldUpdateCourtSpoeAreaOfLaw() throws JsonProcessingException {
        final List<SpoeAreaOfLaw> currentCourtSpoeAreaOfLaw = getCurrentSpoeAreaOflaw();
        final List<SpoeAreaOfLaw> expectedCourtAreasOfLaw = updateCourtAreasOfLaw(currentCourtSpoeAreaOfLaw);
        final String updatedJson = objectMapper().writeValueAsString(expectedCourtAreasOfLaw);
        final String originalJson = objectMapper().writeValueAsString(currentCourtSpoeAreaOfLaw);
        final var response = doPutRequest(
            ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<SpoeAreaOfLaw> updatedCourtAreaOfLaw = response.body().jsonPath().getList(
            ".",
            SpoeAreaOfLaw.class
        );
        assertThat(updatedCourtAreaOfLaw.get(0).getName()).isEqualTo(expectedCourtAreasOfLaw.get(0).getName());
        assertThat(updatedCourtAreaOfLaw.get(0).getId()).isEqualTo(expectedCourtAreasOfLaw.get(0).getId());

        //clean up by removing added record
        final var cleanUpResponse = doPutRequest(
            ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final List<AreaOfLaw> cleanCourtAreaOfLaw = cleanUpResponse.body().jsonPath().getList(
            ".",
            AreaOfLaw.class
        );
        assertThat(cleanCourtAreaOfLaw.get(0).getName()).isEqualTo(currentCourtSpoeAreaOfLaw.get(0).getName());
        assertThat(cleanCourtAreaOfLaw.get(0).getId()).isEqualTo(currentCourtSpoeAreaOfLaw.get(0).getId());

    }

    @Test
    public void shouldRequireATokenWhenUpdatingSpoeAreaOfLawForTheCourt() throws JsonProcessingException {
        final String testJson = objectMapper().writeValueAsString(getCurrentSpoeAreaOflaw());
        final var response = doPutRequest(ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingSpoeAreaOfLawForTheCourt() throws JsonProcessingException {
        final String testJson = objectMapper().writeValueAsString(getCurrentSpoeAreaOflaw());
        final var response = doPutRequest(
            ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldnotUpdateAndReturnNotFoundWhenSpoeCourtDoesNotExist() throws JsonProcessingException {
        final String testJson = objectMapper().writeValueAsString(getCurrentSpoeAreaOflaw());
        final Response response = doPutRequest(
            ABERYSTWYTH_COURT_AREAS_OF_LAW_NOT_FOUND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            testJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldNotUpdateSpoeAreaOfLawForTheCourtThatAlreadyExist() throws JsonProcessingException {
        final String testJson = objectMapper()
            .writeValueAsString(updateCourtAreasOfLaw(updateCourtAreasOfLaw(getCurrentSpoeAreaOflaw())));
        final Response response = doPutRequest(
            ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            testJson
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/

    private List<SpoeAreaOfLaw> getCurrentSpoeAreaOflaw() {
        final var response = doGetRequest(
            ABERYSTWYTH_JUSTICE_CENTRE_SPOE_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        return response.body().jsonPath().getList(".", SpoeAreaOfLaw.class);
    }

    private List<SpoeAreaOfLaw> updateCourtAreasOfLaw(final List<SpoeAreaOfLaw> courtAreaOfLaw) {
        final SpoeAreaOfLaw areaOfLaw = new SpoeAreaOfLaw(TEST_SPOE_AREA_OF_LAW_ID,TEST_SPOE_AREA_OF_LAW_NAME,TEST_SINGLE_POINT_OF_ENTRY);
        final List<SpoeAreaOfLaw> updatedAreaOfLaw = new ArrayList<>(courtAreaOfLaw);
        updatedAreaOfLaw.add(areaOfLaw);
        return updatedAreaOfLaw;
    }

}
