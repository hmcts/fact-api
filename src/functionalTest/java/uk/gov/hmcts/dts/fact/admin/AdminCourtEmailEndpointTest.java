package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
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

    private static final String ALL_EMAILS_PATH = "/emails";
    private static final String ALL_EMAIL_TYPES_PATH = "emailTypes";
    private static final String PLYMOUTH_COMBINED_COURT_SLUG = "plymouth-combined-court";
    private static final String TEST_EMAIL_ADDRESS = "fancy.pancy.email@cat.com";
    private static final String TEST_EMAIL_EXPLANATION = "explanation";
    private static final String TEST_EMAIL_EXPLANATION_CY = "explanation cy";
    private static final int TEST_EMAIL_TYPE = 7;

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
    public void shouldUpdateEmails() throws JsonProcessingException {
        final List<Email> expectedEmails = updateEmails(getCurrentEmails());
        final String json = objectMapper().writeValueAsString(expectedEmails);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + ALL_EMAILS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<Email> updatedOpeningTimes = response.body().jsonPath().getList(".", Email.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedEmails);
    }

    @Test
    public void shouldRequireATokenWhenUpdatingEmails() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(getTestEmails())
            .when()
            .put(ADMIN_COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + ALL_EMAILS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenFromUpdatingEmails() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .body(getTestEmails())
            .when()
            .put(ADMIN_COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + ALL_EMAILS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldGetAllEmailTypes() {

        System.out.println(ADMIN_COURTS_ENDPOINT + ALL_EMAIL_TYPES_PATH);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + ALL_EMAIL_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<EmailType> emailTypes = response.body().jsonPath().getList(".", EmailType.class);
        assertThat(emailTypes).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingEmailTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + ALL_EMAIL_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllEmailTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + ALL_EMAIL_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    private List<Email> getCurrentEmails() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG + ALL_EMAILS_PATH)
            .thenReturn();

        return response.body().jsonPath().getList(".", Email.class);
    }

    private List<Email> updateEmails(List<Email> openingTimes) {
        List<Email> newEmailList;

        final boolean testRecordPresent = openingTimes.stream()
            .map(Email::getAddress)
            .anyMatch(o -> o.equals(TEST_EMAIL_ADDRESS));

        if (testRecordPresent) {
            newEmailList = openingTimes.stream()
                .filter(o -> !o.getAddress().equals(TEST_EMAIL_ADDRESS))
                .collect(toList());
        } else {
            // Add test email
            newEmailList = new ArrayList<>(openingTimes);
            newEmailList.add(new Email(TEST_EMAIL_ADDRESS, TEST_EMAIL_EXPLANATION,
                                       TEST_EMAIL_EXPLANATION_CY, TEST_EMAIL_TYPE));
        }
        return newEmailList;
    }



    private static String getTestEmails() throws JsonProcessingException {
        final List<Email> emails = Arrays.asList(
            new Email("Address 1", "Desc 1", "Desc 1 cy", 1),
            new Email("Address 2", "Desc 2", "Desc 2 cy", 2),
            new Email("Address 3", "Desc 3", "Desc 3 cy", 3)
        );
        return objectMapper().writeValueAsString(emails);
    }
}
