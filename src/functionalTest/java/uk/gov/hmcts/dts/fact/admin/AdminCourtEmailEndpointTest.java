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
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.ADMIN_COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
class AdminCourtEmailEndpointTest extends AdminFunctionalTestBase {

    private static final String ALL_EMAILS_PATH = "/emails";
    private static final String ALL_EMAIL_TYPES_PATH = "emailTypes";
    private static final String BEXLEY_MAGISTRATES_COURT_SLUG = "bexley-magistrates-court";
    private static final String BEXLEY_MAGISTRATES_ALL_EMAILS_PATH = ADMIN_COURTS_ENDPOINT + BEXLEY_MAGISTRATES_COURT_SLUG + ALL_EMAILS_PATH;
    private static final String TEST_EMAIL_ADDRESS = "fancy.pancy.email@cat.com";
    private static final String TEST_EMAIL_EXPLANATION = "explanation";
    private static final String TEST_EMAIL_EXPLANATION_CY = "explanation cy";
    private static final int TEST_EMAIL_TYPE = 7;

    @Test
    void shouldGetEmails() {
        final var response = doGetRequest(BEXLEY_MAGISTRATES_ALL_EMAILS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<Email> openingTimes = response.body().jsonPath().getList(".", Email.class);
        assertThat(openingTimes).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingEmails() {
        final var response = doGetRequest(BEXLEY_MAGISTRATES_ALL_EMAILS_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingEmails() {
        final var response = doGetRequest(BEXLEY_MAGISTRATES_ALL_EMAILS_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldUpdateEmails() throws JsonProcessingException {
        final List<Email> expectedEmails = updateEmails(getCurrentEmails());
        final String json = objectMapper().writeValueAsString(expectedEmails);

        final var response = doPutRequest(BEXLEY_MAGISTRATES_ALL_EMAILS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<Email> updatedOpeningTimes = response.body().jsonPath().getList(".", Email.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedEmails);
    }

    @Test
    void shouldRequireATokenWhenUpdatingEmails() throws JsonProcessingException {
        final var response = doPutRequest(BEXLEY_MAGISTRATES_ALL_EMAILS_PATH, getTestEmails());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenFromUpdatingEmails() throws JsonProcessingException {
        final var response = doPutRequest(BEXLEY_MAGISTRATES_ALL_EMAILS_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken), getTestEmails());
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldGetAllEmailTypes() {
        final var response = doGetRequest(ADMIN_COURTS_ENDPOINT + ALL_EMAIL_TYPES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<EmailType> emailTypes = response.body().jsonPath().getList(".", EmailType.class);
        assertThat(emailTypes).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingEmailTypes() {
        final var response = doGetRequest(ADMIN_COURTS_ENDPOINT + ALL_EMAIL_TYPES_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAllEmailTypes() {
        final var response = doGetRequest(ADMIN_COURTS_ENDPOINT + ALL_EMAIL_TYPES_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    private List<Email> getCurrentEmails() {
        final var response = doGetRequest(BEXLEY_MAGISTRATES_ALL_EMAILS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        return response.body().jsonPath().getList(".", Email.class);
    }

    private List<Email> updateEmails(List<Email> openingTimes) {
        List<Email> newEmailList;

        final boolean testRecordPresent = openingTimes.stream()
            .map(Email::getAddress)
            .anyMatch(TEST_EMAIL_ADDRESS::equals);

        if (testRecordPresent) {
            newEmailList = openingTimes.stream()
                .filter(o -> !TEST_EMAIL_ADDRESS.equals(o.getAddress()))
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
            new Email("Address 1", "Exp 1", "Exp 1 cy", 1),
            new Email("Address 2", "Exp 2", "Exp 2 cy", 2),
            new Email("Address 3", "Exp 3", "Exp 3 cy", 3)
        );
        return objectMapper().writeValueAsString(emails);
    }
}
