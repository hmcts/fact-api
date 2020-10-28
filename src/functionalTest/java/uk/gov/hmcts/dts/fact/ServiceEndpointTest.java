package uk.gov.hmcts.dts.fact;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Service;
import uk.gov.hmcts.dts.fact.model.ServiceArea;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {OAuthClient.class})
@SuppressWarnings("PMD.TooManyMethods")
public class ServiceEndpointTest {

    private static final String CONTENT_TYPE_VALUE = "application/json";

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
    }

    @Test
    public void shouldRetrieveServices() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/services")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<Service> services = asList(response.getBody().as(Service[].class));
        assertThat(services.size()).isGreaterThan(0);
    }


    @Test
    public void shouldRetrieveService() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/services/money")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final Service service = response.getBody().as(Service.class);
        assertThat(service.getName()).isEqualTo("Money");
    }

    @Test
    public void shouldRetrieveServiceAreas() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/services/money/service-areas")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final List<ServiceArea> serviceAreas = asList(response.getBody().as(ServiceArea[].class));
        assertThat(serviceAreas.size()).isGreaterThan(0);
    }
}
