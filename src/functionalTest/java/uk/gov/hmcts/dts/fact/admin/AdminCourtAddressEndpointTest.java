package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.model.admin.CourtSecondaryAddressType;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminCourtAddressEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String COURT_ADDRESS_PATH = "/addresses";
    private static final String PLYMOUTH_COMBINED_COURT_SLUG = "plymouth-combined-court";
    private static final String PLYMOUTH_COMBINED_COURT_ADDRESS_PATH = ADMIN_COURTS_ENDPOINT
        + PLYMOUTH_COMBINED_COURT_SLUG + COURT_ADDRESS_PATH;

    private static final String COURT_SEARCH_ENDPOINT = "/courts/";
    private static final String PLYMOUTH_COMBINED_COURT_PATH = COURT_SEARCH_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG;

    private static final String TEST_POSTCODE = "PL2 2ER";
    private static final String TEST_TOWN_NAME = "town name";
    private static final String TEST_TOWN_NAME_CY = "town name cy";
    private static final int VISIT_US_TYPE_ID = 5880;
    private static final int WRITE_TO_US_TYPE_ID = 5881;
    private static final int COUNTY_ID = 1;
    private static final List<String> TEST_ADDRESS_LINES = Arrays.asList("The Law Courts", "10 Armada Way");
    private static final List<String> TEST_ADDRESS_LINES_CY = Arrays.asList("abc", "abc");
    private static final CourtSecondaryAddressType COURT_SECONDARY_ADDRESS_TYPE_LIST = new CourtSecondaryAddressType(
        Arrays.asList(
            new AreaOfLaw(
                new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
                    34_257, "Civil partnership"), false),
            new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
                34_248, "Adoption"), false)
        ),
        Arrays.asList(
            new CourtType(
                new uk.gov.hmcts.dts.fact.entity.CourtType(11_417, "Family Court")
            ),
            new CourtType(
                new uk.gov.hmcts.dts.fact.entity.CourtType(11_418, "Tribunal")
            )
        )
    );

    private static final String POSTCODE_VALID = "PL6 5DQ";
    private static final String POSTCODES_INVALID = "PL2 56ERR";

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
        final List<CourtAddress> expectedCourtAddress = addNewCourtAddress(currentCourtAddress);
        expectedCourtAddress.get(0).setId(1_960_528);
        final String updatedJson = objectMapper().writeValueAsString(expectedCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        final List<CourtAddress> updatedCourtAddress =
            response.body().jsonPath().getList(".", CourtAddress.class);
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(updatedCourtAddress).containsExactlyElementsOf(expectedCourtAddress);

        //clean up by removing added record
        final String originalJson = objectMapper().writeValueAsString(currentCourtAddress);

        final Response cleanUpResponse = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        expectedCourtAddress.get(0).setId(1_960_530); // +2 compared to above
        final List<CourtAddress> cleanCourtAddress = cleanUpResponse.body().jsonPath().getList(".", CourtAddress.class);
        assertThat(cleanCourtAddress).containsExactlyElementsOf(currentCourtAddress);
    }

    @Test
    public void shouldBeForbiddenForUpdatingCourtAddress() throws JsonProcessingException {
        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final String testJson = objectMapper().writeValueAsString(currentCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingCourtAddress() throws JsonProcessingException {
        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final String testJson = objectMapper().writeValueAsString(currentCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH, testJson);

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotUpdateAddressWithInvalidPostcodes() throws JsonProcessingException {

        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final List<CourtAddress> courtAddressesToBeUpdated = createCourtAddresses();

        final String updatedJson = objectMapper().writeValueAsString(courtAddressesToBeUpdated);

        final var response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        final List<String> invalidPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(invalidPostcodes).containsExactly(POSTCODES_INVALID);

        // Test that the addresses are not updated, i.e. the original addresses remain
        final List<CourtAddress> updatedCourtAddresses = getCurrentCourtAddress();
        assertThat(updatedCourtAddresses.size()).isEqualTo(currentCourtAddress.size());
        assertThat(updatedCourtAddresses.get(0)).isNotEqualTo(courtAddressesToBeUpdated.get(0));
        assertThat(updatedCourtAddresses.get(0)).isEqualTo(currentCourtAddress.get(0));
    }

    @Test
    public void shouldUpdateCoordinatesWhenPostcodeIsChanged() throws JsonProcessingException {
        // Call the court endpoint to get the current coordinates
        Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        Court court = response.as(Court.class);
        final Double originalLat = court.getLat();
        final Double originalLon = court.getLon();

        // Get the current court addresses and update the first address with a new postcode
        final List<CourtAddress> originalCourtAddresses = getCurrentCourtAddress();
        final String originalCourtAddressesJson = objectMapper().writeValueAsString(originalCourtAddresses);
        final List<CourtAddress> expectedCourtAddresses = updateCourtAddressesWithNewPostcode(
            originalCourtAddresses,
            POSTCODE_VALID
        );
        final String updatedJson = objectMapper().writeValueAsString(expectedCourtAddresses);

        response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            updatedJson
        );
        final List<CourtAddress> updatedCourtAddresses = response.body().jsonPath().getList(".", CourtAddress.class);
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(updatedCourtAddresses.get(0).getPostcode()).isEqualTo(POSTCODE_VALID);

        // Call the court endpoint again to get the new updated coordinates
        response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        court = response.as(Court.class);
        assertThat(court.getLat()).isNotEqualTo(originalLat);
        assertThat(court.getLon()).isNotEqualTo(originalLon);

        // Clean up by reverting to original postcode for the court addresses
        response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            originalCourtAddressesJson
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/

    private List<CourtAddress> getCurrentCourtAddress() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.body().jsonPath().getList(".", CourtAddress.class);
    }

    private List<CourtAddress> addNewCourtAddress(final List<CourtAddress> courtAddresses) {
        final List<CourtAddress> updatedCourtAddress = new ArrayList<>(courtAddresses);
        updatedCourtAddress.add(new CourtAddress(
            1_960_529,
            VISIT_US_TYPE_ID,
            TEST_ADDRESS_LINES,
            TEST_ADDRESS_LINES_CY,
            TEST_TOWN_NAME,
            TEST_TOWN_NAME_CY,
            COUNTY_ID,
            TEST_POSTCODE,
            COURT_SECONDARY_ADDRESS_TYPE_LIST
        ));
        return updatedCourtAddress;
    }

    private List<CourtAddress> updateCourtAddressesWithNewPostcode(final List<CourtAddress> courtAddresses, final String postcode) {
        final List<CourtAddress> updatedCourtAddresses = new ArrayList<>(courtAddresses);
        updatedCourtAddresses.get(0).setPostcode(postcode);
        return updatedCourtAddresses;
    }

    private List<CourtAddress> createCourtAddresses() {
        return Arrays.asList(
            new CourtAddress(
                null,
                VISIT_US_TYPE_ID,
                TEST_ADDRESS_LINES,
                TEST_ADDRESS_LINES_CY,
                TEST_TOWN_NAME,
                TEST_TOWN_NAME_CY,
                COUNTY_ID,
                POSTCODE_VALID,
                COURT_SECONDARY_ADDRESS_TYPE_LIST
            ),
            new CourtAddress(
                null,
                WRITE_TO_US_TYPE_ID,
                TEST_ADDRESS_LINES,
                TEST_ADDRESS_LINES_CY,
                TEST_TOWN_NAME,
                TEST_TOWN_NAME_CY,
                COUNTY_ID,
                POSTCODES_INVALID,
                COURT_SECONDARY_ADDRESS_TYPE_LIST
            )
        );
    }
}
