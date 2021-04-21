package uk.gov.hmcts.dts.fact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.util.FunctionalTestBase;
import uk.gov.hmcts.dts.fact.util.OAuthClient;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {OAuthClient.class})
public class ServiceAreaEndpointTest extends FunctionalTestBase {

    @Test
    public void shouldRetrieveServiceArea() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/service-areas/money-claims")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final ServiceArea serviceArea = response.getBody().as(ServiceArea.class);
        assertThat(serviceArea.getName()).isEqualTo("Money claims");
    }
}
