package uk.gov.hmcts.dts.fact;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.util.FunctionalTestBase;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
class MigrationEndpointTest extends FunctionalTestBase {

    private static final String MIGRATION_ENDPOINT = "/private-migration/export";

    @Test
    @DisplayName("Should return 200 OK status from migration endpoint")
    void shouldReturnOkStatus() {
        final var response = doGetRequest(MIGRATION_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    @Test
    @DisplayName("Should return JSON with exactly 9 top-level keys")
    void shouldHave9TopLevelKeys() {
        final var response = doGetRequest(MIGRATION_ENDPOINT);
        assertThat(response.jsonPath().getMap("$").keySet())
            .hasSize(9)
            .containsExactlyInAnyOrder(
                "courts",
                "local_authority_types",
                "service_areas",
                "services",
                "contact_description_types",
                "opening_hour_types",
                "court_types",
                "regions",
                "area_of_law_types"
            );
    }

    @Test
    @DisplayName("Should convert all IDs to strings instead of integers")
    void shouldConvertIdsToStrings() {
        final var response = doGetRequest(MIGRATION_ENDPOINT);
        String courtId = response.jsonPath().getString("courts[0].id");
        String serviceAreaId = response.jsonPath().getString("service_areas[0].id");
        String regionId = response.jsonPath().getString("regions[0].id");

        assertThat(courtId).isNotNull().isInstanceOf(String.class);
        assertThat(serviceAreaId).isNotNull().isInstanceOf(String.class);
        assertThat(regionId).isNotNull().isInstanceOf(String.class);
    }

    @Test
    @DisplayName("Should return expected DX codes for Bristol court")
    void shouldReturnBristolCourtWithCombinedDxCodes() {
        final var response = doGetRequest(MIGRATION_ENDPOINT);

        final String expectedSlug = "bristol-civil-and-family-justice-centre";
        final String expectedName = "Bristol Civil and Family Justice Centre";
        final String expectedDxCode1 = "95903 Bristol 3";
        final String expectedDxCode2 = "95905 Bristol 3";
        final String expectedExplanation1 = "civil";
        final String expectedExplanation2 = "family";

        final String actualName = response.jsonPath()
            .getString("courts.find { it.slug == '" + expectedSlug + "' }.name");
        final Boolean isOpen = response.jsonPath()
            .getBoolean("courts.find { it.slug == '" + expectedSlug + "' }.open");

        final List<String> dxCodes = response.jsonPath()
            .getList("courts.find { it.slug == '" + expectedSlug + "' }.court_dx_codes.dx_code", String.class);
        final List<String> dxExplanations = response.jsonPath()
            .getList("courts.find { it.slug == '" + expectedSlug + "' }.court_dx_codes.explanation", String.class);
        final List<String> dxIds = response.jsonPath()
            .getList("courts.find { it.slug == '" + expectedSlug + "' }.court_dx_codes.id", String.class);

        assertThat(actualName).isEqualTo(expectedName);
        assertThat(isOpen).isTrue();

        assertThat(dxCodes).isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrder(expectedDxCode1, expectedDxCode2);

        assertThat(dxExplanations).isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrder(expectedExplanation1, expectedExplanation2);

        assertThat(dxIds).isNotNull()
            .hasSize(2)
            .allMatch(Objects::nonNull);
    }

    @Test
    @DisplayName("Should return East London Tribunal court with all basic fields populated")
    void shouldReturnEastLondonTribunalWithBasicFields() {
        final var response = doGetRequest(MIGRATION_ENDPOINT);

        final String expectedSlug = "east-london-tribunal-hearing-centre";
        final String expectedName = "East London Tribunal Hearing Centre";
        final String expectedRegionId = "7";
        final Boolean expectedOpen = true;
        final Boolean expectedIsServiceCentre = false;

        final String actualName = response.jsonPath()
            .getString("courts.find { it.slug == '" + expectedSlug + "' }.name");
        final Boolean actualOpen = response.jsonPath()
            .getBoolean("courts.find { it.slug == '" + expectedSlug + "' }.open");
        final String actualRegionId = response.jsonPath()
            .getString("courts.find { it.slug == '" + expectedSlug + "' }.region_id");
        final Boolean actualIsServiceCentre = response.jsonPath()
            .getBoolean("courts.find { it.slug == '" + expectedSlug + "' }.is_service_centre");

        assertThat(actualName).isEqualTo(expectedName);
        assertThat(actualOpen).isEqualTo(expectedOpen);
        assertThat(actualRegionId).isEqualTo(expectedRegionId);
        assertThat(actualIsServiceCentre).isEqualTo(expectedIsServiceCentre);
    }

    @Test
    @DisplayName("Should return Salisbury court with local authorities grouped by area of law")
    void shouldReturnSalisburyCourtWithGroupedLocalAuthorities() {
        final var response = doGetRequest(MIGRATION_ENDPOINT);

        final String expectedSlug = "salisbury-law-courts";
        final String expectedName = "Salisbury Law Courts";
        final int expectedGroupCount = 3;

        final String actualName = response.jsonPath()
            .getString("courts.find { it.slug == '" + expectedSlug + "' }.name");
        final List<Integer> areaOfLawIds = response.jsonPath()
            .getList("courts.find { it.slug == '" + expectedSlug + "' }.court_local_authorities.area_of_law_id",
                     Integer.class);
        final List<String> groupIds = response.jsonPath()
            .getList("courts.find { it.slug == '" + expectedSlug + "' }.court_local_authorities.id",
                     String.class);

        assertThat(actualName).isEqualTo(expectedName);

        assertThat(areaOfLawIds).isNotNull()
            .hasSize(expectedGroupCount)
            .containsExactlyInAnyOrder(34255, 34253, 34254);

        assertThat(groupIds).isNotNull()
            .hasSize(expectedGroupCount)
            .allMatch(Objects::nonNull);

        final List<Integer> firstGroupLocalAuthorities = response.jsonPath()
            .getList("courts.find { it.slug == '" + expectedSlug + "' }.court_local_authorities[0]"
                         + ".local_authority_ids", Integer.class);
        assertThat(firstGroupLocalAuthorities).isNotNull()
            .isNotEmpty()
            .contains(397339, 397297, 397301);
    }

    @Test
    @DisplayName("Should return Isle of Wight court with multiple fax numbers")
    void shouldReturnIsleOfWightCourtWithMultipleFaxNumbers() {
        final var response = doGetRequest(MIGRATION_ENDPOINT);

        final String expectedSlug = "isle-of-wight-combined-court";
        final String expectedFax1 = "0870 761 7624";
        final String expectedFax2 = "0870 761 7625";
        final String expectedFax3 = "08707617627";
        final String expectedFax4 = "08707617626";

        final List<String> faxNumbers = response.jsonPath()
            .getList("courts.find { it.slug == '" + expectedSlug + "' }.court_fax.fax_number", String.class);
        final List<String> faxIds = response.jsonPath()
            .getList("courts.find { it.slug == '" + expectedSlug + "' }.court_fax.id", String.class);

        assertThat(faxNumbers).isNotNull()
            .hasSize(4)
            .containsExactlyInAnyOrder(expectedFax1, expectedFax2, expectedFax3, expectedFax4);

        assertThat(faxIds).isNotNull()
            .hasSize(4)
            .allMatch(Objects::nonNull);
    }
}
