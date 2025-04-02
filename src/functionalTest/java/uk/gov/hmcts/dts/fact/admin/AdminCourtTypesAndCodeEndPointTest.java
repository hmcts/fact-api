package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.model.admin.CourtTypesAndCodes;
import uk.gov.hmcts.dts.fact.model.admin.DxCode;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith({SpringExtension.class})
@SuppressWarnings("PMD.TooManyMethods")
class AdminCourtTypesAndCodeEndPointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String COURT_TYPES_PATH = "courtTypes";
    private static final String COURT_TYPES_AND_CODE_PATH = "/courtTypesAndCodes";
    private static final String ALL_COURT_TYPES_FULL_PATH = ADMIN_COURTS_ENDPOINT + COURT_TYPES_PATH;
    private static final String AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG = "aylesbury-county-court-and-family-court";
    private static final String WOLVERHAMTON_COMBINED_COURT_CENTER_COURT_SLUG = "wolverhampton-combined-court-centre";
    private static final String AYLESBURY_COURT_TYPES_AND_CODE_PATH = ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + COURT_TYPES_AND_CODE_PATH;
    private static final String WOLVERHAMTON_COURT_TYPES_AND_CODE_PATH = ADMIN_COURTS_ENDPOINT + WOLVERHAMTON_COMBINED_COURT_CENTER_COURT_SLUG + COURT_TYPES_AND_CODE_PATH;
    private static final String TEST_GBS_CODE = "Y288";

    private static final List<CourtType> EXPECTED_COURT_TYPE_CODES = Arrays.asList(
        new CourtType(11_419,"County Court","County",123)
    );

    private static final List<DxCode> EXPECTED_COURT_DX_CODES = Arrays.asList(
        new DxCode("702019 Wolverhampton 4","test1","test1")
    );

    /************************************************************* GET request tests section. ***************************************************************/

    @Test
    void returnAllCourtTypes() {
        final var response = doGetRequest(
            ALL_COURT_TYPES_FULL_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtType> courtTypes = response.body().jsonPath().getList(".", CourtType.class);
        assertThat(courtTypes).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingAllCourtTypes() {
        final var response = doGetRequest(
            ALL_COURT_TYPES_FULL_PATH
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAllCourtTypes() {
        final var response = doGetRequest(
            ALL_COURT_TYPES_FULL_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* Court type and code GET request tests section. ***************************************************************/

    @Test
    void shouldReturnCourtTypesAndCodes() {
        final var response = doGetRequest(
            AYLESBURY_COURT_TYPES_AND_CODE_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final CourtTypesAndCodes courtTypesAndCodesListExpected = response.as(CourtTypesAndCodes.class);
        assertThat(courtTypesAndCodesListExpected.getDxCodes()).isNotNull();
        assertThat(courtTypesAndCodesListExpected.getGbsCode()).isNotNull();
    }

    @Test
    void shouldRequireATokenWhenGettingCourtTypesAndCodes() {
        final var response = doGetRequest(
            AYLESBURY_COURT_TYPES_AND_CODE_PATH
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingCourtTypesAndCodes() {
        final var response = doGetRequest(
            AYLESBURY_COURT_TYPES_AND_CODE_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
    /************************************************************* Court type and code PUT request tests section. ***************************************************************/

    @Test
    void shouldUpdateCourtTypesAndCode() throws JsonProcessingException {

        final var response = doGetRequest(
            WOLVERHAMTON_COURT_TYPES_AND_CODE_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final CourtTypesAndCodes courtTypesAndCodesListOriginal = response.as(CourtTypesAndCodes.class);
        final String originalJson = objectMapper().writeValueAsString(courtTypesAndCodesListOriginal);
        final CourtTypesAndCodes courtTypesAndCodesListExpected = updateTest();
        final String expectedJson = objectMapper().writeValueAsString(courtTypesAndCodesListExpected);

        final var updatedResponse = doPutRequest(
            WOLVERHAMTON_COURT_TYPES_AND_CODE_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            expectedJson);

        assertThat(updatedResponse.statusCode()).isEqualTo(OK.value());
        final CourtTypesAndCodes updatedCourtTypesAndCodes = updatedResponse.as(CourtTypesAndCodes.class);

        assertThat(courtTypesAndCodesListExpected).isEqualTo(updatedCourtTypesAndCodes);

        //cleanup
        final var cleanUpResponse = doPutRequest(
            WOLVERHAMTON_COURT_TYPES_AND_CODE_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson);
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        final CourtTypesAndCodes cleanUpCourtTypesAndCodes = cleanUpResponse.as(CourtTypesAndCodes.class);
        assertThat(cleanUpCourtTypesAndCodes).isEqualTo(courtTypesAndCodesListOriginal);

    }

    @Test
    void shouldRequireATokenWhenUpdatingCourtTypesAndCode() throws JsonProcessingException {
        final CourtTypesAndCodes currentCourtTypesAndCodes = getCurrentCourtTypesAndCodes();
        final String testJson = objectMapper().writeValueAsString(currentCourtTypesAndCodes);

        final var response = doPutRequest(
            WOLVERHAMTON_COURT_TYPES_AND_CODE_PATH,
            testJson);

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForUpdatingCourtTypesAndCode() throws JsonProcessingException {
        final CourtTypesAndCodes currentCourtTypesAndCodes = getCurrentCourtTypesAndCodes();
        final String testJson = objectMapper().writeValueAsString(currentCourtTypesAndCodes);

        final var response = doPutRequest(
            WOLVERHAMTON_COURT_TYPES_AND_CODE_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken),
            testJson);

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldNotUpdatingCourtTypesAndCodeForCourtDoesNotExist() throws JsonProcessingException {
        final CourtTypesAndCodes currentCourtTypesAndCodes = getCurrentCourtTypesAndCodes();
        final String testJson = objectMapper().writeValueAsString(currentCourtTypesAndCodes);

        final var response = doPutRequest(
            ADMIN_COURTS_ENDPOINT + "wolverhampton-combined" + COURT_TYPES_AND_CODE_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            testJson
        );

        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /********************************************** Utility functions. *****************************************************************************/

    private CourtTypesAndCodes updateTest() {

        return new CourtTypesAndCodes(EXPECTED_COURT_TYPE_CODES,TEST_GBS_CODE, EXPECTED_COURT_DX_CODES);
    }

    private CourtTypesAndCodes getCurrentCourtTypesAndCodes() {
        final Response response = doGetRequest(
            WOLVERHAMTON_COURT_TYPES_AND_CODE_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.as(CourtTypesAndCodes.class);
    }
}
