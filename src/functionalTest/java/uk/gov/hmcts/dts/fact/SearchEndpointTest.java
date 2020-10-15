package uk.gov.hmcts.dts.fact;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt2;

import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ExtendWith(SpringExtension.class)
public class SearchEndpointTest {

    private static final String CONTENT_TYPE_VALUE = "application/json";

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
    }

    @Test
    public void shouldRetrieve10CourtDetailsSortedByDistance() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/search/results.json?postcode=OX1 1RZ")
            .thenReturn();


        assertThat(response.statusCode()).isEqualTo(200);
        final List<OldCourt2> courts = response.body().jsonPath().getList(".", OldCourt2.class);
        assertThat(courts.size()).isEqualTo(10);
        assertThat(courts).isSortedAccordingTo(Comparator.comparing(OldCourt2::getDistance));
    }

    @Test
    public void shouldRetrieve10CourtDetailsByAreaOfLawSortedByDistance() {
        final String aol = "Adoption";
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/search/results.json?postcode=OX1 1RZ&aol=" +  aol)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<OldCourt2> courts = response.body().jsonPath().getList(".", OldCourt2.class);
        assertThat(courts.size()).isEqualTo(10);
        assertThat(courts).isSortedAccordingTo(Comparator.comparing(OldCourt2::getDistance));
        assertThat(courts.stream().allMatch(c -> c.getAreasOfLaw().contains(aol)));
    }
}
