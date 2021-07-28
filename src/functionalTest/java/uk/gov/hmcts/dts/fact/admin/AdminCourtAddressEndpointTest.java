package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)

public class AdminCourtAddressEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String COURT_ADDRESS_PATH = "addresses";
    private static final String PLYMOUTH_COMBINED_COURT_SLUG = "plymouth-combined-court/";
    private static final String PLYMOUTH_COMBINED_COURT_ADDRESS_PATH = ADMIN_COURTS_ENDPOINT
        + PLYMOUTH_COMBINED_COURT_SLUG + COURT_ADDRESS_PATH;

    private static final String TEST_POSTCODE = "PL2 2ER";
    private static final String TEST_TOWN_NAME = "testname";
    private static final String TEST_TOWN_NAME_CY = "explanation cy";
    private static final int TEST_TYPE_ID = 5881;
    private static final List<String> TEST_ADDRESS_LINES = Arrays.asList("The Law Courts", "10 Armada Way");
    private static final List<String> TEST_ADDRESS_LINES_CY = Arrays.asList("abc","abc");

    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void returnAddressForTheCourt() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtAddress> courtAddress = response.body().jsonPath().getList(".", CourtAddress.class);
        assertThat(courtAddress).isNotEmpty();
    }

    @Test
    public void shouldRequireATokenWhenGettingAddressForTheCourt() {
        final Response response = doGetRequest(PLYMOUTH_COMBINED_COURT_ADDRESS_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAddressForTheCourt() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    public void shouldUpdateAddress() throws JsonProcessingException {

        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final List<CourtAddress> expectedCourtAddress = updateCourtAddress(currentCourtAddress);
        final String updatedJson = objectMapper().writeValueAsString(expectedCourtAddress);
        final String originalJson = objectMapper().writeValueAsString(currentCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtAddress> updatedCourtAddress = response.body().jsonPath().getList(".", CourtAddress.class);

        assertThat(updatedCourtAddress).containsExactlyElementsOf(expectedCourtAddress);


        //clean up by removing added record
        final Response cleanUpResponse = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final List<CourtAddress> cleanCourtAddress = cleanUpResponse.body().jsonPath().getList(".",CourtAddress.class);

        assertThat(cleanCourtAddress).containsExactlyElementsOf(currentCourtAddress);

    }

    @Test
    public void adminShouldBeForbiddenForUpdatingCourtAddress() throws JsonProcessingException {
        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final String testJson = objectMapper().writeValueAsString(currentCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken),testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void adminShouldBeUnauthorisedForUpdatingCourtAddress() throws JsonProcessingException {
        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final String testJson = objectMapper().writeValueAsString(currentCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH, testJson);

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    private List<CourtAddress> getCurrentCourtAddress() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.body().jsonPath().getList(".", CourtAddress.class);
    }
    /************************************************************* Shared utility methods. ***************************************************************/

    private List<CourtAddress> updateCourtAddress(final List<CourtAddress> courtAddresses) {
        final List<CourtAddress> updatedCourtAddress = new ArrayList<>(courtAddresses);
        CourtAddress courtAddress = new CourtAddress();
        courtAddress.setPostcode(TEST_POSTCODE);
        courtAddress.setTownName(TEST_TOWN_NAME);
        courtAddress.setTownNameCy(TEST_TOWN_NAME_CY);
        courtAddress.setAddressTypeId(TEST_TYPE_ID);
        courtAddress.setAddressLines(TEST_ADDRESS_LINES);
        courtAddress.setAddressLinesCy(TEST_ADDRESS_LINES_CY);

        updatedCourtAddress.add(courtAddress);

        return updatedCourtAddress;
    }

}
