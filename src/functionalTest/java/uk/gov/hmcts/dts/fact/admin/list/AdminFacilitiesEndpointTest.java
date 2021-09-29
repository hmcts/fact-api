package uk.gov.hmcts.dts.fact.admin.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(SpringExtension.class)
public class AdminFacilitiesEndpointTest extends AdminFunctionalTestBase {

    private static final String GET_FACILITIES_ENDPOINT = "/admin/facilities/";

    private static final int TEST_INTERVIEW_ROOM_ID = 42;
    private static final int TEST_INTERVIEW_ROOM_ID_NOT_FOUND = 12_345;
    private static final String TEST_FACILITY_NAME = "Interview room";
    private static final String TEST_FACILITY_NAMECY = "Ystafell gyfweld";
    private static final String TEST_IMAGE = "interview";
    private static final String TEST_IMAGE_DESCRIPTION = "Interview room icon.";
    private static final String TEST_IMAGE_FILE_PATH = "uploads/facility/image_file/53/interview.png";
    private static final int TEST_FACILITY_ORDER = 9;
    private static final String REORDER = "reorder";

    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void shouldReturnAllFacilities() {
        final Response response = doGetRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<FacilityType> facilities = response.body().jsonPath().getList(".", FacilityType.class);
        assertThat(facilities).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingFacilities() {
        final Response response = doGetRequest(GET_FACILITIES_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingFacilities() {
        final Response response = doGetRequest(GET_FACILITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }


    @Test
    public void shouldReturnFacility() {
        final Response response = doGetRequest(
            GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final FacilityType facilityType = response.as(FacilityType.class);
        assertThat(facilityType).isNotNull();
        assertThat(facilityType.getId()).isEqualTo(TEST_INTERVIEW_ROOM_ID);
    }

    @Test
    public void shouldRequireATokenWhenGettingFacilityType() {
        final Response response = doGetRequest(GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingFacilityType() {
        final Response response = doGetRequest(
            GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldReturnFacilityTypeNotFound() {
        final Response response = doGetRequest(
            GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID_NOT_FOUND,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    public void shouldUpdateFacilityType() throws JsonProcessingException {
        final FacilityType currentFacilityType = getCurrentFacility();
        final FacilityType expectedFacilityType = getUpdatedInterviewRoomFacility();

        final String updatedJson = objectMapper().writeValueAsString(expectedFacilityType);
        final String originalJson = objectMapper().writeValueAsString(currentFacilityType);

        final Response response = doPutRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final FacilityType updatedFacilityType = response.as(FacilityType.class);
        assertThat(updatedFacilityType).isEqualTo(expectedFacilityType);

        //clean up by removing added record
        final Response cleanUpResponse = doPutRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        final FacilityType cleanFacilityType = cleanUpResponse.as(FacilityType.class);
        final String cleanUpJson = objectMapper().writeValueAsString(cleanFacilityType);
        assertThat(cleanUpJson).isEqualTo(originalJson);
    }

    @Test
    public void shouldBeForbiddenForUpdatingFacilityType() throws JsonProcessingException {
        final FacilityType currentFacilityType = getCurrentFacility();
        final String testJson = objectMapper().writeValueAsString(currentFacilityType);

        final Response response = doPutRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingFacilityType() throws JsonProcessingException {
        final FacilityType currentFacilityType = getCurrentFacility();
        final String testJson = objectMapper().writeValueAsString(currentFacilityType);

        final Response response = doPutRequest(
            GET_FACILITIES_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotFoundForUpdateWhenFacilityTypeDoesNotExist() throws JsonProcessingException {
        final FacilityType currentFacilityType = getCurrentFacility();
        currentFacilityType.setId(1234);
        final String testJson = objectMapper().writeValueAsString(currentFacilityType);

        final Response response = doPutRequest(
            GET_FACILITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            testJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* POST request tests section. ***************************************************************/

    @Test
    public void shouldCreateFacilityType() throws JsonProcessingException {

        final List<FacilityType> currentFacilityTypes = getCurrentFacilityTypes();
        final FacilityType expectedFacilityType = getUpdatedInterviewRoomFacility();
        expectedFacilityType.setName("TEST");
        final String newFacilityTypeJson = objectMapper().writeValueAsString(expectedFacilityType);

        final var response = doPostRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newFacilityTypeJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        final FacilityType createdFacilityType = response.as(FacilityType.class);

        assertThat(createdFacilityType.getName()).isEqualTo(expectedFacilityType.getName());
        final String deleteJson = objectMapper().writeValueAsString(createdFacilityType);

        //clean up by removing added record
        final var cleanUpResponse = doDeleteRequest(
            GET_FACILITIES_ENDPOINT + createdFacilityType.getId(),
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            deleteJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        final var responseAfterClean = doGetRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );

        final List<FacilityType> cleanedFacilitiesTypes = responseAfterClean.body().jsonPath().getList(
            ".",
            FacilityType.class
        );
        assertThat(cleanedFacilitiesTypes).containsExactlyElementsOf(currentFacilityTypes);

    }

    @Test
    public void shouldBeForbiddenForCreatingFacilityType() throws JsonProcessingException {
        final FacilityType currentFacility = getCurrentFacility();
        final String testJson = objectMapper().writeValueAsString(currentFacility);
        final Response response = doPostRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenCreatingFacilityType() throws JsonProcessingException {
        final FacilityType currentFacility = getCurrentFacility();
        final String testJson = objectMapper().writeValueAsString(currentFacility);

        final Response response = doPostRequest(
            GET_FACILITIES_ENDPOINT, testJson
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotCreateFacilityTypeThatAlreadyExist() throws JsonProcessingException {
        final FacilityType currentFacility = getCurrentFacility();
        final String testJson = objectMapper().writeValueAsString(currentFacility);

        final Response response = doPostRequest(
             GET_FACILITIES_ENDPOINT,
             Map.of(AUTHORIZATION, BEARER + superAdminToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* Reorder Update Request Tests. ***************************************************************/

    @Test
    public void shouldUpdateReorderFacilityTypes() throws JsonProcessingException {

        final Response response = doGetRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<FacilityType> facilities = response.body().jsonPath().getList(".", FacilityType.class);
        assertThat(facilities).hasSizeGreaterThan(1);
        final List<Integer> idListSwap = facilities.stream().sorted(Comparator.comparingInt(f -> f.getOrder())).map(f -> f.getId()).collect(toList());
        final List<Integer> originalIdList = new ArrayList<>(idListSwap);
        int size = facilities.size();

        //swapping two id to reorder facility types
        Collections.swap(idListSwap, 0, size - 1);

        final String expectedJson = objectMapper().writeValueAsString(idListSwap);
        final Response responseResetId = doPutRequest(
            GET_FACILITIES_ENDPOINT + REORDER,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            expectedJson
        );

        assertThat(responseResetId.statusCode()).isEqualTo(OK.value());
        final List<FacilityType> resetSwapIdList = responseResetId.body().jsonPath().getList(".", FacilityType.class);
        final List<Integer> resetIdList = resetSwapIdList.stream().sorted(Comparator.comparingInt(f -> f.getOrder())).map(f -> f.getId()).collect(toList());
        assertThat(resetIdList).isEqualTo(idListSwap);
        assertThat(resetIdList.get(0)).isEqualTo(originalIdList.get(size - 1));
    }

    @Test
    public void shouldBeForbiddenForUpdatingReorderFacilityTypes() throws JsonProcessingException {
        final List<FacilityType> currentFacilityTypes = getCurrentFacilityTypes();
        final List<Integer> idList = currentFacilityTypes.stream().map(f -> f.getId()).collect(toList());
        final String testJson = objectMapper().writeValueAsString(idList);

        final Response response = doPutRequest(
            GET_FACILITIES_ENDPOINT + REORDER,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingReorderFacilityType() throws JsonProcessingException {
        final List<FacilityType> currentFacilityTypes = getCurrentFacilityTypes();
        final List<Integer> idList = currentFacilityTypes.stream().map(f -> f.getId()).collect(toList());
        final String testJson = objectMapper().writeValueAsString(idList);

        final Response response = doPutRequest(
            GET_FACILITIES_ENDPOINT + REORDER, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotUpdateWhenReorderFacilityTypeDoesNotExist() throws JsonProcessingException {
        final List<FacilityType> currentFacilityTypes = getCurrentFacilityTypes();
        final List<Integer> idList = currentFacilityTypes.stream().map(f -> f.getId()).collect(toList());
        idList.set(1,2000);
        final String testJson = objectMapper().writeValueAsString(idList);

        final Response response = doPutRequest(
            GET_FACILITIES_ENDPOINT + REORDER,
            Map.of(AUTHORIZATION, BEARER + superAdminToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Delete request tests section. ***************************************************************/

    @Test
    public void adminShouldBeForbiddenForDeletingFacilityType() throws JsonProcessingException {

        final var response = doDeleteRequest(
            GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID_NOT_FOUND,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), getTestFacilityType()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenDeletingFacilityType() throws JsonProcessingException {
        final var response = doDeleteRequest(
            GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID_NOT_FOUND,
            getTestFacilityType()
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotDeleteFacilityTypeNotFound() throws JsonProcessingException {

        final var response = doDeleteRequest(
            GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID_NOT_FOUND,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestFacilityType()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldNotDeleteFacilityTypeInUse() throws JsonProcessingException {

        final var response = doDeleteRequest(
            GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestFacilityType()
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/

    private FacilityType getCurrentFacility() {
        final Response response = doGetRequest(
            GET_FACILITIES_ENDPOINT + TEST_INTERVIEW_ROOM_ID,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        return response.as(FacilityType.class);
    }

    private String getTestFacilityType() throws JsonProcessingException {
        return objectMapper().writeValueAsString(getUpdatedInterviewRoomFacility());
    }

    private FacilityType getUpdatedInterviewRoomFacility() {
        return new FacilityType(
            TEST_INTERVIEW_ROOM_ID,
            TEST_FACILITY_NAME,
            TEST_FACILITY_NAMECY,
            TEST_IMAGE,
            TEST_IMAGE_DESCRIPTION,
            TEST_IMAGE_FILE_PATH,
            TEST_FACILITY_ORDER
        );
    }

    private List<FacilityType> getCurrentFacilityTypes() {
        final var response = doGetRequest(
            GET_FACILITIES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        return response.body().jsonPath().getList(".", FacilityType.class);
    }

}
