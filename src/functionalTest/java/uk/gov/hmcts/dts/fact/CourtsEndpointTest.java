package uk.gov.hmcts.dts.fact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.util.FunctionalTestBase;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.http.HttpStatus.*;

@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith({SpringExtension.class})
public class CourtsEndpointTest extends FunctionalTestBase {

    private static final String AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT
        = "aylesbury-magistrates-court-and-family-court";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE
        = "birmingham-civil-and-family-justice-centre";
    private static final String COURT_DETAIL_BY_SLUG_ENDPOINT = "/courts/";
    private static final String COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT = "/courts/search";
    private static final String OLD_COURT_DETAIL_BY_SLUG_ENDPOINT = "/courts/%s.json";
    private static final String COURT_SEARCH_ENDPOINT = "/courts";

    protected static final String CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL = "cardiff-social-security-and-child-support-tribunal";

    @Test
    public void shouldRetrieveCourtDetail() {
        final var response = doGetRequest(format(OLD_COURT_DETAIL_BY_SLUG_ENDPOINT, AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final OldCourt court = response.as(OldCourt.class);
        assertThat(court.getSlug()).isEqualTo(AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT);
    }

    @Test
    public void shouldRetrieveCourtReferenceByPartialQuery() {
        final String name = "Oxford Combined Court Centre";
        final String slug = "oxford-combined-court-centre";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=Oxford Combine");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByFullQuery() {
        final String name = "Oxford Combined Court Centre";
        final String slug = "oxford-combined-court-centre";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=Oxford Combined Court Centre");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByPartialPostCodeQuery() {
        final String name = "Skipton County Court and Family Court";
        final String slug = "skipton-county-court-and-family-court";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=BD23");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByFullPostCodeQuery() {
        final String name = "Skipton County Court and Family Court";
        final String slug = "skipton-county-court-and-family-court";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=BD23 1RH");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldReturnBadRequestForEmptyQuery() {
        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=");
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldRetrieveCourtDetailBySlug() {
        final var response = doGetRequest(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final Court court = response.as(Court.class);
        assertThat(court.getSlug()).isEqualTo(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE);
    }

    @Test
    public void shouldRetrieveCourtsByPrefixWhereDisplayedFalseAndCaseLower() {
        final var response = doGetRequest(COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT + "?prefix=a&active=false");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courtReferences = response.body().jsonPath().getList(".", CourtReference.class);
        Assertions.assertTrue(courtReferences.stream().allMatch(c -> c.getName().charAt(0) == 'A'));
        Assertions.assertTrue(courtReferences.stream().allMatch(c -> c.getSlug().charAt(0) == 'a'));
    }

    @Test
    public void shouldRetrieveCourtsByPrefixWhereDispayedTrueAndCaseUpper() {
        final var response = doGetRequest(COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT + "?prefix=B&active=true");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courtReferences = response.body().jsonPath().getList(".", CourtReference.class);
        Assertions.assertTrue(courtReferences.stream().allMatch(c -> c.getName().charAt(0) == 'B'));
        Assertions.assertTrue(courtReferences.stream().allMatch(c -> c.getSlug().charAt(0) == 'b'));
    }

    @Test
    public void shouldReturnAnErrorWhenSizeConstraintBreached() {
        final var response = doGetRequest(COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT + "?prefix=mosh&active=true");
        assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void shouldReturnAnErrorWhenRequiredParamMissing() {
        final var response = doGetRequest(COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT + "?prefix=kupo");
        assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void shouldRetrieveCourtDetailInWelsh() {
        final var response = doGetRequest(format(OLD_COURT_DETAIL_BY_SLUG_ENDPOINT, CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL),
                                          Map.of(ACCEPT_LANGUAGE, "cy"));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final OldCourt court = response.as(OldCourt.class);
        assertThat(court.getAddresses().get(0).getTownName()).isEqualTo("Caerdydd");
    }

    @Test
    public void shouldRetrieveCourtDetailBySlugInWelsh() {
        final var response = doGetRequest(COURT_DETAIL_BY_SLUG_ENDPOINT + CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL,
                                          Map.of(ACCEPT_LANGUAGE, "cy"));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final Court court = response.as(Court.class);
        assertThat(court.getAddresses().get(0).getTownName()).isEqualTo("Caerdydd");
    }

    @Test
    public void shouldNotRetrieveClosedCourts() {
        final String slug = "aylesbury-crown-court";
        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=aylesbury");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldNotReturnDuplicatesForCourtsWithMultipleAddresses() {
        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=Darlington Magistrates' Court and Family Court");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.size()).isEqualTo(1);
    }
}
