package uk.gov.hmcts.dts.fact.admin;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;
@ExtendWith({SpringExtension.class})
@SuppressWarnings("PMD.TooManyMethods")
public class AdminCourtTypesEndpointTest extends AdminFunctionalTestBase {


    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String COURT_TYPES_PATH = "/courtTypes";
    private static final String ALL_COURT_TYPES_PATH = "courtTypes/all";
    private static final String AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG = "aylesbury-county-court-and-family-court";
    private static final String TEST = "Crown Court";
    private static final Integer TEST_ID = 11420;


    @Test
    public void returnAllCourtTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + ALL_COURT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtType> courtTypes = response.body().jsonPath().getList(".", CourtType.class);
        assertThat(courtTypes).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllCourtTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + ALL_COURT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllCourtTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + ALL_COURT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void returnCourtCourtTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + COURT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtType> courtTypes = response.body().jsonPath().getList(".", CourtType.class);
        assertThat(courtTypes).hasSizeGreaterThan(1);
    }


    @Test
    public void shouldRequireATokenWhenGettingCourtTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + COURT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingCourtTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT  + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + COURT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldUpdateCourtCourtTypes() throws JsonProcessingException {
        final List<CourtType> currentCourtCourtTypes = getCurrentCourtCourtTypes();
        final List<CourtType> expectedCourtCourtTypes = updateCourtCourtTypes(currentCourtCourtTypes);
        final String updatedJson = objectMapper().writeValueAsString(expectedCourtCourtTypes);
        final String originalJson = objectMapper().writeValueAsString(currentCourtCourtTypes);

        final var response = getResponse(updatedJson);
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtType> updatedCourtCourtTypes = response.body().jsonPath().getList(".", CourtType.class);
        assertThat(updatedCourtCourtTypes).containsExactlyElementsOf(expectedCourtCourtTypes);

        //clean up by removing added record
        final var cleanUpResponse = getResponse(originalJson);
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        final List<CourtType> cleanCourtCourtTypes = cleanUpResponse.body().jsonPath().getList(".", CourtType.class);
        assertThat(cleanCourtCourtTypes).containsExactlyElementsOf(currentCourtCourtTypes);
    }

    @Test
    public void shouldRequireATokenWhenUpdatingCourtCourtTypes() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(getTestCourtCourtTypesJson())
            .when()
            .put(ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + COURT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingCourtCourtTypes() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .body(getTestCourtCourtTypesJson())
            .when()
            .put(ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + COURT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    private Response getResponse(String json){

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + COURT_TYPES_PATH)
            .thenReturn();

        return response;

    }

    private List<CourtType> getCurrentCourtCourtTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + COURT_TYPES_PATH)
            .thenReturn();

        return response.body().jsonPath().getList(".", CourtType.class);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<CourtType> updateCourtCourtTypes(List<CourtType> courtTypes) {
        List<CourtType> updatedCourtCourtTypes = new ArrayList<>(courtTypes);

            CourtType courtType = new CourtType();
            courtType.setName(TEST);
            courtType.setId(TEST_ID);
            updatedCourtCourtTypes.add(courtType);

        return updatedCourtCourtTypes;
    }

    private static String getTestCourtCourtTypesJson() throws JsonProcessingException {
        final List<CourtType> courtTypes = Arrays.asList(
            new CourtType(),
            new CourtType()

        );
        return objectMapper().writeValueAsString(courtTypes);
    }
}
