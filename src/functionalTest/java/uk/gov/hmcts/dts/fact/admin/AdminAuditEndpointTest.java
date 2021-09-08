package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.Audit;
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminAuditEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_AUDIT_ENDPOINT = "/admin/audit/";
    private static final String OPENING_TIMES_PATH = "/openingTimes";
    private static final String ADMINISTRATIVE_COURT_SLUG = "administrative-court";
    private static final String ADMINISTRATIVE_COURT_OPENING_TIMES_PATH = ADMIN_COURTS_ENDPOINT + ADMINISTRATIVE_COURT_SLUG + OPENING_TIMES_PATH;
    private static final String TEST_HOURS = "test hour";
    private static final int BAILIFF_OFFICE_OPEN_TYPE_ID = 5;
    private static final OpeningTime TEST_OPENING_TIME = new OpeningTime(BAILIFF_OFFICE_OPEN_TYPE_ID, TEST_HOURS);
    private static final String TEST_AUDIT_NAME = "Update court opening times";

    /************************************************************* Get Request Tests. ****************************************************/

    @Test
    public void shouldReturnAllAuditsForPageAndSize() throws JsonProcessingException {
        setUpOpeningTimes();
        checkAuditData("", "", "", "");
    }

    @Test
    public void shouldReturnAllAuditsForLocation() throws JsonProcessingException {
        setUpOpeningTimes();
        checkAuditData(ADMINISTRATIVE_COURT_SLUG, "", "", "");
    }

    @Test
    public void shouldReturnAllAuditsForLocationAndEmail() throws JsonProcessingException {
        setUpOpeningTimes();
        checkAuditData(ADMINISTRATIVE_COURT_SLUG, "hmcts.fact@gmail.com",
                       "", "");
    }

    @Test
    public void shouldReturnAllAuditsForLocationEmailAndToAndFromDates() throws JsonProcessingException {
        setUpOpeningTimes();
        checkAuditData(ADMINISTRATIVE_COURT_SLUG, "hmcts.fact@gmail.com",
                       "2020-01-01T01:01:01.111", "2520-01-01T01:01:01.111");
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void shouldReturnPaginatedResultsForPageAndSize() throws JsonProcessingException {

        setUpOpeningTimes();

        final List<Audit> currentAudits = getCurrentAudits(0,200_000, "", "", "", "");
        assertThat(currentAudits).isNotEmpty();
        float maxSizePerPage = Math.round(currentAudits.size() / 2.0);

        // Test pagination by requesting two separate pages based on the total amount returned above.
        // For example, total audits = 27. Request page 1, 14 size. Request page 2 13 size.
        for (int i = 0; i < 2; i++) {

            final List<Audit> currentPaginatedAudits = getCurrentAudits(i, (int) maxSizePerPage, "", "", "", "");
            assertThat(currentPaginatedAudits).isNotEmpty();
            boolean  sizeValid = currentPaginatedAudits.size() == maxSizePerPage || currentPaginatedAudits.size() == maxSizePerPage - 1;
            assertThat(sizeValid).isEqualTo(true);
        }
    }

    @Test
    public void shouldRequireATokenWhenGettingAllAudits() {
        final Response response = doGetRequest(ADMIN_AUDIT_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllAudits() {
        final Response response = doGetRequest(
            ADMIN_AUDIT_ENDPOINT + "?page=0&size=20000",
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* utility methods. ***************************************************************/

    private void setUpOpeningTimes() throws JsonProcessingException {
        Response response = doGetRequest(ADMINISTRATIVE_COURT_OPENING_TIMES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        final List<OpeningTime> currentOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);

        // Action: Adding new opening time
        List<OpeningTime> expectedOpeningTimes = addNewOpeningTime(currentOpeningTimes);
        String openingTimeJson = objectMapper().writeValueAsString(expectedOpeningTimes);

        response = doPutRequest(ADMINISTRATIVE_COURT_OPENING_TIMES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), openingTimeJson);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        List<OpeningTime> updatedOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedOpeningTimes);

        // Action: Removing the added opening time
        expectedOpeningTimes = removeOpeningTime(updatedOpeningTimes);
        openingTimeJson = objectMapper().writeValueAsString(expectedOpeningTimes);

        response = doPutRequest(ADMINISTRATIVE_COURT_OPENING_TIMES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), openingTimeJson);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        updatedOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedOpeningTimes);
    }

    private void checkAuditData(String location, String email, String dateFrom, String dateTo) {
        final List<Audit> currentAudits = getCurrentAudits(0, 200_000, location, email, dateFrom, dateTo);
        assertThat(currentAudits).isNotEmpty();

        int auditListSize = currentAudits.size();
        int indexActionDataBefore = auditListSize - 2;
        int indexActionDataAfter = auditListSize - 1;

        String actionDataBeforeName = currentAudits.get(indexActionDataBefore).getAction().getName();
        LocalDateTime lastAuditTime = currentAudits.get(indexActionDataAfter).getCreationTime();

        assertThat(LocalDateTime.now().minusSeconds(5).isBefore(lastAuditTime)).isEqualTo(true);
        assertThat(currentAudits.get(indexActionDataBefore).getActionDataBefore()).isEqualTo(currentAudits.get(indexActionDataAfter).getActionDataAfter());
        assertThat(actionDataBeforeName).isEqualTo(currentAudits.get(indexActionDataAfter).getAction().getName());
        assertThat(actionDataBeforeName).isEqualTo(TEST_AUDIT_NAME);
    }

    private List<Audit> getCurrentAudits(int page, int size, String location, String email,
                                         String dateFrom, String dateTo) {
        final var response = doGetRequest(
            ADMIN_AUDIT_ENDPOINT + "?page=" + page + "&size=" + size + "&location="
                + location + "&email=" + email + "&dateFrom=" + dateFrom + "&dateTo=" + dateTo,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
        return response.body().jsonPath().getList(".", Audit.class);
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
}
