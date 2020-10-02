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
    private static final String CONTENT_TYPE_VALUE = "application/json";

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
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/courts/" + AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT + ".json")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final Court court = response.as(Court.class);
        assertThat(court.getSlug()).isEqualTo(AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT);

    }

    @Test
    public void shouldRetrieveCourtReferenceByPartialQuery() {
        final String name = "Oxford Combined Court Centre";
        final String slug = "oxford-combined-court-centre";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/courts?q=Oxford Combine")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByFullQuery() {
        final String name = "Oxford Combined Court Centre";
        final String slug = "oxford-combined-court-centre";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/courts?q=Oxford Combined Court Centre")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByPartialPostCodeQuery() {
        final String name = "Skipton Magistrates' Court";
        final String slug = "skipton-magistrates-court";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/courts?q=BD23")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveCourtReferenceByFullPostCodeQuery() {
        final String name = "Skipton Magistrates' Court";
        final String slug = "skipton-magistrates-court";

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/courts?q=BD23 1RH")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    public void shouldRetrieveNoCourtReferenceByEmptyQuery() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/courts?q=")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts).isEmpty();
    }
}
