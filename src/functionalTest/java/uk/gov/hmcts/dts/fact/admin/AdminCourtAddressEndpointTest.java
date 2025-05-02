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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
class AdminCourtAddressEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String COURT_ADDRESS_PATH = "/addresses";
    private static final String PLYMOUTH_COMBINED_COURT_SLUG = "plymouth-combined-court";
    private static final String PLYMOUTH_COMBINED_COURT_ADDRESS_PATH = ADMIN_COURTS_ENDPOINT
        + PLYMOUTH_COMBINED_COURT_SLUG + COURT_ADDRESS_PATH;

    private static final String COURT_SEARCH_ENDPOINT = "/courts/";
    private static final String PLYMOUTH_COMBINED_COURT_PATH = COURT_SEARCH_ENDPOINT + PLYMOUTH_COMBINED_COURT_SLUG;
    private static final String DELETE_LOCK_BY_EMAIL_PATH = ADMIN_COURTS_ENDPOINT + "hmcts.super.fact@gmail.com/lock";

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
                new uk.gov.hmcts.dts.fact.entity.CourtType(11_417, "Family Court","Family")
            ),
            new CourtType(
                new uk.gov.hmcts.dts.fact.entity.CourtType(11_418, "Tribunal", "Tribunal")
            )
        )
    );

    private static final String POSTCODE_VALID = "PL6 5DQ";
    private static final String POSTCODES_INVALID = "PL2 56ERR";

    private static final Integer SORT_ORDER_1 = 0;
    private static final Integer SORT_ORDER_2 = 1;
    private static final String EPIM_ID = "epim-id";


    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    void returnAddressForTheCourt() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtAddress> courtAddress = response.body().jsonPath().getList(".", CourtAddress.class);
        assertThat(courtAddress).isNotEmpty();
    }

    @Test
    void shouldRequireATokenWhenGettingAddressForTheCourt() {
        final Response response = doGetRequest(PLYMOUTH_COMBINED_COURT_ADDRESS_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAddressForTheCourt() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    void shouldUpdateAddress() throws JsonProcessingException {
        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final List<CourtAddress> expectedCourtAddress = addNewCourtAddress(currentCourtAddress);
        final String updatedJson = objectMapper().writeValueAsString(expectedCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        final List<CourtAddress> updatedCourtAddress =
            response.body().jsonPath().getList(".", CourtAddress.class);
        expectedCourtAddress.get(0).setId(updatedCourtAddress.get(0).getId());
        Integer updatedId = updatedCourtAddress.get(1).getId();
        expectedCourtAddress.get(1).setId(updatedId);
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
        expectedCourtAddress.get(0).setId(updatedId + 1); // +1 compared to above
        final List<CourtAddress> cleanCourtAddress = cleanUpResponse.body().jsonPath().getList(".", CourtAddress.class);
        assertThat(cleanCourtAddress).containsExactlyElementsOf(currentCourtAddress);
    }

    @Test
    void shouldBeForbiddenForUpdatingCourtAddress() throws JsonProcessingException {
        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final String testJson = objectMapper().writeValueAsString(currentCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenUpdatingCourtAddress() throws JsonProcessingException {
        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final String testJson = objectMapper().writeValueAsString(currentCourtAddress);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH, testJson);

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotUpdateAddressWithInvalidPostcodes() throws JsonProcessingException {

        final List<CourtAddress> currentCourtAddress = getCurrentCourtAddress();
        final List<CourtAddress> courtAddressesToBeUpdated = createCourtAddresses();

        final String updatedJson = objectMapper().writeValueAsString(courtAddressesToBeUpdated);

        final var response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        final String invalidPostcodes = response.body().jsonPath().getString("message");
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(invalidPostcodes).isEqualTo(POSTCODES_INVALID);

        // Test that the addresses are not updated, i.e. the original addresses remain
        final List<CourtAddress> updatedCourtAddresses = getCurrentCourtAddress();
        assertThat(updatedCourtAddresses.size()).isEqualTo(currentCourtAddress.size());
        assertThat(updatedCourtAddresses.get(0)).isNotEqualTo(courtAddressesToBeUpdated.get(0));
        assertThat(updatedCourtAddresses.get(0)).isEqualTo(currentCourtAddress.get(0));
    }

    @Test
    void shouldUpdateCoordinatesWhenPostcodeIsChanged() throws JsonProcessingException {

        //calling user delete lock endpoint to remove lock for the court
        final var delResponse = doDeleteRequest(DELETE_LOCK_BY_EMAIL_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
                                                "");
        assertThat(delResponse.statusCode()).isEqualTo(OK.value());

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

    private List<CourtAddress> getCurrentCourtAddress() throws JsonProcessingException {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDRESS_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(false)
            .as("Response body: " + response.getBody())
            .isTrue();
        assertThat(response.statusCode())
            .as("Unexpected status code. Response body: " + response.getBody().asString())
            .isEqualTo(OK.value());
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
            COURT_SECONDARY_ADDRESS_TYPE_LIST,
            SORT_ORDER_2,
            EPIM_ID
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
                COURT_SECONDARY_ADDRESS_TYPE_LIST,
                SORT_ORDER_1,
                EPIM_ID
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
                COURT_SECONDARY_ADDRESS_TYPE_LIST,
                SORT_ORDER_2,
                EPIM_ID
            )
        );
    }
}
