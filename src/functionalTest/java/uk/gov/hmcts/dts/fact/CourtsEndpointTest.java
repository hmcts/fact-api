package uk.gov.hmcts.dts.fact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.util.FunctionalTestBase;
import uk.gov.hmcts.dts.fact.util.OAuthClient;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {OAuthClient.class})
@SuppressWarnings("PMD.TooManyMethods")
public class CourtsEndpointTest extends FunctionalTestBase {

    private static final String AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT
        = "aylesbury-magistrates-court-and-family-court";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE
        = "birmingham-civil-and-family-justice-centre";
    private static final String CONTENT_TYPE_VALUE = "application/json";

    private static final String COURT_DETAIL_BY_SLUG_ENDPOINT = "/courts/";
    private static final String OLD_COURT_DETAIL_BY_SLUG_ENDPOINT = "/courts/%s.json";
    private static final String COURT_SEARCH_ENDPOINT = "/courts";

    protected static final String CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL = "cardiff-social-security-and-child-support-tribunal";

    @Test
    public void shouldRetrieveCourtDetail() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(format(OLD_COURT_DETAIL_BY_SLUG_ENDPOINT, AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT))
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final OldCourt court = response.as(OldCourt.class);
        assertThat(court.getSlug()).isEqualTo(AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT);
    }

    @Test
    public void shouldRetrieveCourtReferenceByPartialQuery() {
        final String name = "Oxford Combined Court Centre";
        final String slug = "oxford-combined-court-centre";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURT_SEARCH_ENDPOINT + "?q=Oxford Combine")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByFullQuery() {
        final String name = "Oxford Combined Court Centre";
        final String slug = "oxford-combined-court-centre";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURT_SEARCH_ENDPOINT + "?q=Oxford Combined Court Centre")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByPartialPostCodeQuery() {
        final String name = "Skipton County Court and Family Court";
        final String slug = "skipton-county-court-and-family-court";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURT_SEARCH_ENDPOINT + "?q=BD23")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByFullPostCodeQuery() {
        final String name = "Skipton County Court and Family Court";
        final String slug = "skipton-county-court-and-family-court";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURT_SEARCH_ENDPOINT + "?q=BD23 1RH")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldReturnBadRequestForEmptyQuery() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURT_SEARCH_ENDPOINT + "?q=")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldRetrieveCourtDetailBySlug() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Court court = response.as(Court.class);
        assertThat(court.getSlug()).isEqualTo(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE);
    }

    @Test
    public void shouldRetrieveCourtDetailInWelsh() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(ACCEPT_LANGUAGE, "cy")
            .when()
            .get(format(OLD_COURT_DETAIL_BY_SLUG_ENDPOINT, CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL))
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final OldCourt court = response.as(OldCourt.class);
        assertThat(court.getAddresses().get(0).getTownName()).isEqualTo("Caerdydd");
    }

    @Test
    public void shouldRetrieveCourtDetailBySlugInWelsh() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(ACCEPT_LANGUAGE, "cy")
            .when()
            .get(COURT_DETAIL_BY_SLUG_ENDPOINT + CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Court court = response.as(Court.class);
        assertThat(court.getAddresses().get(0).getTownName()).isEqualTo("Caerdydd");
    }

    @Test
    public void shouldNotRetrieveClosedCourts() {
        final String slug = "aylesbury-crown-court";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURT_SEARCH_ENDPOINT + "?q=aylesbury")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldNotReturnDuplicatesForCourtsWithMultipleAddresses() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURT_SEARCH_ENDPOINT + "?q=Darlington Magistrates' Court and Family Court")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.size()).isEqualTo(1);
    }
}
