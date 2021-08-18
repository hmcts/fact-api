package uk.gov.hmcts.dts.fact.admin.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AdminAreasOfLawEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_AREAS_OF_LAW_ENDPOINT = "/admin/areasOfLaw/";

    private static final int AREA_OF_LAW_ID_HOUSING_POSSESSION = 34_253;
    private static final String TEST_NAME = "Housing possession";
    private static final String TEST_ALT_NAME = "Housing1234";
    private static final String TEST_ALT_NAME_CY = "Tai";
    private static final String TEST_DISPLAY_NAME = "Housing234";
    private static final String TEST_DISPLAY_NAME_CY = "Tai1234";
    private static final Boolean TEST_SINGLE_POINT_OF_ENTRY = false;
    private static final String TEST_EXTERNAL_LINK = "https%3A//www.gov.uk/evicting-tenants";
    private static final String TEST_EXTERNAL_LINK_DESC = "Information about evicting tenants";
    private static final String TEST_EXTERNAL_LINK_DESC_CY = "Gwybodaeth ynglŷn â troi tenantiaid allan";
    private static final String TEST_DISPLAY_EXTERNAL_LINK = null;

    @Autowired
    private AreasOfLawRepository areasOfLawRepository;

    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void shouldReturnAllAreasOfLaw() {
        final Response response = doGetRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AreaOfLaw> courtAreasOfLaw = response.body().jsonPath().getList(".", AreaOfLaw.class);
        assertThat(courtAreasOfLaw).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllCourtAreasOfLaw() {
        final Response response = doGetRequest(ADMIN_AREAS_OF_LAW_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllCourtAreasOfLaw() {
        final Response response = doGetRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldReturnAreaOfLaw() {
        final Response response = doGetRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT + AREA_OF_LAW_ID_HOUSING_POSSESSION,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final AreaOfLaw areaOfLaw = response.as(AreaOfLaw.class);
        assertThat(areaOfLaw).isNotNull();
        assertThat(areaOfLaw.getId()).isEqualTo(AREA_OF_LAW_ID_HOUSING_POSSESSION);
    }

    @Test
    public void shouldRequireATokenWhenGettingAreaOfLaw() {
        final Response response = doGetRequest(ADMIN_AREAS_OF_LAW_ENDPOINT + AREA_OF_LAW_ID_HOUSING_POSSESSION);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAreaOfLaw() {
        final Response response = doGetRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT + AREA_OF_LAW_ID_HOUSING_POSSESSION,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldReturnAreaOfLawNotFound() {
        final Response response = doGetRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT + "12345",
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    public void shouldUpdateAreaOfLaw() throws JsonProcessingException {
        final AreaOfLaw currentAreaOfLaw = getCurrentAreaOfLaw();
        final AreaOfLaw expectedAreaOfLaw = updateAreaOfLaw();
        final String updatedJson = objectMapper().writeValueAsString(expectedAreaOfLaw);
        final String originalJson = objectMapper().writeValueAsString(currentAreaOfLaw);

        final Response response = doPutRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final AreaOfLaw updatedAreaOfLaw = response.as(AreaOfLaw.class);
        assertThat(updatedAreaOfLaw).isEqualTo(expectedAreaOfLaw);

        //clean up by removing added record
        final Response cleanUpResponse = doPutRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        final AreaOfLaw cleanAreaOfLaw = cleanUpResponse.as(AreaOfLaw.class);
        final String cleanUpJson = objectMapper().writeValueAsString(cleanAreaOfLaw);
        assertThat(cleanUpJson).isEqualTo(originalJson);
    }

    @Test
    public void shouldBeForbiddenForUpdatingAreaOfLaw() throws JsonProcessingException {
        final AreaOfLaw currentAreaOfLaw = getCurrentAreaOfLaw();
        final String testJson = objectMapper().writeValueAsString(currentAreaOfLaw);

        final Response response = doPutRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken),testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingAreaOfLaw() throws JsonProcessingException {
        final AreaOfLaw currentAreaOfLaw = getCurrentAreaOfLaw();
        final String testJson = objectMapper().writeValueAsString(currentAreaOfLaw);

        final Response response = doPutRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotFoundForUpdateWhenAreaOflawDoesNotExist() throws JsonProcessingException {
        final AreaOfLaw currentAreaOfLaw = getCurrentAreaOfLaw();
        currentAreaOfLaw.setId(1234);
        final String testJson = objectMapper().writeValueAsString(currentAreaOfLaw);

        final Response response = doPutRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            testJson);
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldReturnBadRequestForUpdateAreaofLawWhenIdIsNull() throws JsonProcessingException {
        final AreaOfLaw currentAreaOfLaw = getCurrentAreaOfLaw();
        currentAreaOfLaw.setId(null);
        final String testJson = objectMapper().writeValueAsString(currentAreaOfLaw);
        final Response response = doPutRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            testJson);
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /************************************************************* POST request tests section. ***************************************************************/
    @Test
    public void shouldCreateAreaOfLaw() throws JsonProcessingException {
        final AreaOfLaw newAreaOfLaw = getCurrentAreaOfLaw();
        newAreaOfLaw.setName("House possession111");
        final String originalJson = objectMapper().writeValueAsString(newAreaOfLaw);
        final var response = doPostRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());

        // Clean up database - remove created area of law
        final AreaOfLaw areaOfLaw = response.as(AreaOfLaw.class);
        areasOfLawRepository.deleteById(areaOfLaw.getId());
    }

    /************************************************************* Shared utility methods. ***************************************************************/

    private AreaOfLaw getCurrentAreaOfLaw() {
        final Response response = doGetRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT + AREA_OF_LAW_ID_HOUSING_POSSESSION,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.as(AreaOfLaw.class);
    }

    private AreaOfLaw updateAreaOfLaw() {

        return new AreaOfLaw(AREA_OF_LAW_ID_HOUSING_POSSESSION,TEST_NAME,TEST_SINGLE_POINT_OF_ENTRY,TEST_EXTERNAL_LINK,
                                                          TEST_EXTERNAL_LINK_DESC,TEST_EXTERNAL_LINK_DESC_CY,TEST_ALT_NAME,TEST_ALT_NAME_CY,TEST_DISPLAY_NAME,
                                                          TEST_DISPLAY_NAME_CY,TEST_DISPLAY_EXTERNAL_LINK);
    }
}
