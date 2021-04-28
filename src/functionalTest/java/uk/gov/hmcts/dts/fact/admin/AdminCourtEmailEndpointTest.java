package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminCourtEmailEndpointTest extends AdminFunctionalTestBase {

    private static final String OPENING_TIMES_PATH = "/emails";
    private static final String OPENING_TYPES_PATH = "/emailTypes";



    private static final String ALL_EMAILS_PATH = "/emails";
    private static final String ALL_EMAIL_TYPES_PATH = "/emailTypes";
    private static final String PLYMOUTH_COMBINED_COURT_SLUG = "plymouth-combined-court";
    private static final Integer TEST_OPENING_TYPE_ID = 9999;
    private static final String TEST_HOURS = "test hour";

    @Test
    public void shouldGetEmails() {

        String path = ADMIN_COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + ALL_EMAILS_PATH;
        System.out.println(path);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + ALL_EMAILS_PATH)
            .thenReturn();

        System.out.println(BEARER + authenticatedToken);

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<Email> openingTimes = response.body().jsonPath().getList(".", Email.class);
        assertThat(openingTimes).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingEmails() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + ALL_EMAILS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingEmails() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + ALL_EMAILS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldUpdateOpeningTimes() throws JsonProcessingException {
        final List<OpeningTime> currentOpeningTimes = getCurrentOpeningTimes();
        final List<OpeningTime> expectedOpeningTimes = updateOpeningTimes(currentOpeningTimes);
        final String json = objectMapper().writeValueAsString(expectedOpeningTimes);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<OpeningTime> updatedOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedOpeningTimes);
    }

    @Test
    public void shouldRequireATokenWhenUpdatingOpeningTimes() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(getTestOpeningTimeJson())
            .when()
            .put(COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + OPENING_TIMES_PATH)
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
            .put(COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + OPENING_TIMES_PATH)
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
            .get(COURTS_ENDPOINT + OPENING_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<OpeningType> openingTypes = response.body().jsonPath().getList(".", OpeningType.class);
        assertThat(openingTypes).hasSizeGreaterThan(1);
    }

    private List<OpeningTime> getCurrentOpeningTimes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + OPENING_TIMES_PATH)
            .thenReturn();

        return response.body().jsonPath().getList(".", OpeningTime.class);
    }

    private List<OpeningTime> updateOpeningTimes(List<OpeningTime> openingTimes) {
        List<OpeningTime> updatedOpeningTimes;

        final boolean testRecordPresent = openingTimes.stream()
            .map(o -> o.getHours())
            .anyMatch(o -> o.equals(TEST_HOURS));

        if (testRecordPresent) {
            updatedOpeningTimes = openingTimes.stream()
                .filter(o -> !o.getHours().equals(TEST_HOURS))
                .collect(toList());
        } else {
            updatedOpeningTimes = new ArrayList<>(openingTimes);
            updatedOpeningTimes.add(new OpeningTime(TEST_OPENING_TYPE_ID, TEST_HOURS));
        }
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
