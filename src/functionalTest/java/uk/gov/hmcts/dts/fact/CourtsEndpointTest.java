package uk.gov.hmcts.dts.fact;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        final uk.gov.hmcts.dts.fact.model.Court court = response.as(uk.gov.hmcts.dts.fact.model.Court.class);
        assertThat(court.getSlug()).isEqualTo(AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT);

    }
}
