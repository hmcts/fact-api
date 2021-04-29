package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminCourtOpeningTimeEndpointTest extends AdminFunctionalTestBase {

    private static final String OPENING_TIMES_PATH = "/" + "openingTimes";
    private static final String OPENING_TYPES_PATH = "openingTypes";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG = "birmingham-civil-and-family-justice-centre";
    private static final String TEST_HOURS = "test hour";
    private static final int BAILIFF_OFFICE_OPEN_TYPE_ID = 5;
    private static final String BAILIFF_OFFICE_OPEN_TYPE = "Bailiff office open";
    private static final String BAILIFF_OFFICE_OPEN_TYPE_WELSH = "Oriau agor swyddfa'r Beiliaid";
    private static final OpeningTime TEST_OPENING_TIME = new OpeningTime(BAILIFF_OFFICE_OPEN_TYPE_ID, TEST_HOURS);

    @Test
    public void shouldGetOpeningTimes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<OpeningTime> openingTimes = response.body().jsonPath().getList(".", OpeningTime.class);
        assertThat(openingTimes).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingOpeningTimes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingOpeningTimes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldAddOrRemoveOpeningTimes() throws JsonProcessingException {
        // Add a new opening time
        final List<OpeningTime> currentOpeningTimes = getCurrentOpeningTimes(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);
        List<OpeningTime> expectedOpeningTimes = addNewOpeningTime(currentOpeningTimes);
        String json = objectMapper().writeValueAsString(expectedOpeningTimes);

        var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        List<OpeningTime> updatedOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedOpeningTimes);

        // Check the standard court endpoint still display the added opening type in English after the opening time update
        response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());

        Court court = response.as(Court.class);
        List<uk.gov.hmcts.dts.fact.model.OpeningTime> openingTimes = court.getOpeningTimes();
        assertThat(openingTimes).hasSizeGreaterThan(1);
        assertThat(openingTimes.stream()).anyMatch(openingTime -> openingTime.getHours().equals(TEST_HOURS)
            && openingTime.getType().equals(BAILIFF_OFFICE_OPEN_TYPE));

        // Check the standard court endpoint still display the added opening type in Welsh after the opening time update
        response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(ACCEPT_LANGUAGE, "cy")
            .when()
            .get(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());

        court = response.as(Court.class);
        openingTimes = court.getOpeningTimes();
        assertThat(openingTimes).hasSizeGreaterThan(1);
        assertThat(openingTimes.stream()).anyMatch(openingTime -> openingTime.getHours().equals(TEST_HOURS)
            && openingTime.getType().equals(BAILIFF_OFFICE_OPEN_TYPE_WELSH));

        // Remove the added opening time
        expectedOpeningTimes = removeOpeningTime(updatedOpeningTimes);
        json = objectMapper().writeValueAsString(expectedOpeningTimes);

        response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        updatedOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedOpeningTimes);
    }

    @Test
    public void shouldRequireATokenWhenUpdatingOpeningTimes() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(getTestOpeningTimeJson())
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingOpeningTimes() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .body(getTestOpeningTimeJson())
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldGetAllOpeningTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + OPENING_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<OpeningType> openingTypes = response.body().jsonPath().getList(".", OpeningType.class);
        assertThat(openingTypes).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllOpeningTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + OPENING_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    private List<OpeningTime> getCurrentOpeningTimes(final String slug) {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + slug + OPENING_TIMES_PATH)
            .thenReturn();

        return response.body().jsonPath().getList(".", OpeningTime.class);
    }

    private List<OpeningTime> addNewOpeningTime(List<OpeningTime> openingTimes) {
        List<OpeningTime> updatedOpeningTimes = new ArrayList<>(openingTimes);
        updatedOpeningTimes.add(TEST_OPENING_TIME);
        return updatedOpeningTimes;
    }

    private List<OpeningTime> removeOpeningTime(List<OpeningTime> openingTimes) {
        List<OpeningTime> updatedOpeningTimes = new ArrayList<>(openingTimes);
        updatedOpeningTimes.removeIf(time -> time.getHours().equals(TEST_HOURS));
        return updatedOpeningTimes;
    }

    private static String getTestOpeningTimeJson() throws JsonProcessingException {
        final List<OpeningTime> openingTimes = Arrays.asList(
            new OpeningTime(2, "Monday to Friday 9.00am - 5.00pm"),
            new OpeningTime(5, "Monday to Friday 9.00am - 3.30pm"),
            new OpeningTime(9, "Monday to Friday 9.00am - 4.30pm")
        );
        return objectMapper().writeValueAsString(openingTimes);
    }
}
