package uk.gov.hmcts.dts.fact;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ExtendWith(SpringExtension.class)
public class CourtsEndpointTest {

    private static final String AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT
        = "aylesbury-magistrates-court-and-family-court";

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
    }

    @Test
    public void shouldRetrieveCourtDetail() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .when()
            .get("/courts/" + AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT + ".json")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final Court court = response.as(Court.class);
        assertThat(court.getSlug()).isEqualTo(AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT);

    }

    @Test
    public void shouldRetrieveCourtReferenceByQueryAberdeen() {
        final String name = "Aberdeen Tribunal Hearing Centre";
        final String slug = "aberdeen-tribunal-hearing-centre";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .when()
            .get("/courts?q=Aberdeen")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldNotRetrieveCourtReferenceWithNotExactNameAberdeen() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .when()
            .get("/courts?q=Aberdee")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts).isEmpty();
    }

    @Test
    public void shouldRetrieveCourtReferenceWitFullTownAmersham() {
        final String name = "Amersham Law Courts";
        final String slug = "amersham-law-courts";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .when()
            .get("/courts?q=Amersham")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldNotRetrieveCourtReferenceWithNotExactTownAmersham() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .when()
            .get("/courts?q=Amer")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts).isEmpty();
    }

    @Test
    public void shouldRetrieveCourtReferenceWitPartialAddressQuery() {
        final String name = "Ashford Tribunal Hearing Centre";
        final String slug = "ashford-tribunal-hearing-centre";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .when()
            .get("/courts?q=Ashford Tribunal")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }
}
