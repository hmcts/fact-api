package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.Audit;
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

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
    public void shouldReturnAllAudits() throws JsonProcessingException {

        var response = doGetRequest(ADMINISTRATIVE_COURT_OPENING_TIMES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        final List<OpeningTime> currentOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);

        // Action: Adding new opening time
        List<OpeningTime> expectedOpeningTimes = addNewOpeningTime(currentOpeningTimes);
        String json = objectMapper().writeValueAsString(expectedOpeningTimes);

        response = doPutRequest(ADMINISTRATIVE_COURT_OPENING_TIMES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        List<OpeningTime> updatedOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedOpeningTimes);

        // Action: Removing the added opening time
        expectedOpeningTimes = removeOpeningTime(updatedOpeningTimes);
        json = objectMapper().writeValueAsString(expectedOpeningTimes);

        response = doPutRequest(ADMINISTRATIVE_COURT_OPENING_TIMES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        updatedOpeningTimes = response.body().jsonPath().getList(".", OpeningTime.class);
        assertThat(updatedOpeningTimes).containsExactlyElementsOf(expectedOpeningTimes);

        final List<Audit> currentAudits = getCurrentAudits();
        assertThat(currentAudits).isNotEmpty();
        assertThat(currentAudits.get(currentAudits.size() - 2).getActionDataBefore()).isEqualTo(currentAudits.get(currentAudits.size() - 1).getActionDataAfter());
        assertThat(currentAudits.get(currentAudits.size() - 2).getAction().getName()).isEqualTo(currentAudits.get(currentAudits.size() - 1).getAction().getName());
        assertThat(currentAudits.get(currentAudits.size() - 2).getAction().getName()).isEqualTo(TEST_AUDIT_NAME);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllAudits() {
        final Response response = doGetRequest(ADMIN_AUDIT_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllAudits() {
        final Response response = doGetRequest(
            ADMIN_AUDIT_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* utility methods. ***************************************************************/

    private List<Audit> getCurrentAudits() {
        final var response = doGetRequest(
            ADMIN_AUDIT_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
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
