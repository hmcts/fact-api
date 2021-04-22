package uk.gov.hmcts.dts.fact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Service;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.util.FunctionalTestBase;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith({SpringExtension.class})
public class ServiceEndpointTest extends FunctionalTestBase {

    @Test
    public void shouldRetrieveServices() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/services")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
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

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Service service = response.getBody().as(Service.class);
        assertThat(service.getSlug()).isEqualTo("money");
        assertThat(service.getName()).isEqualTo("Money");
        assertThat(service.getServiceAreas().size()).isGreaterThan(0);
    }

    @Test
    public void shouldRetrieveServiceAreasSortedbySortOrder() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/services/money/service-areas")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<ServiceArea> serviceAreas = asList(response.getBody().as(ServiceArea[].class));
        assertThat(serviceAreas.size()).isGreaterThan(0);
        assertThat(serviceAreas.size()).isGreaterThan(0);
        assertThat(serviceAreas.get(0).getName()).isEqualTo("Money claims");
        assertThat(serviceAreas.get(1).getName()).isEqualTo("Probate");
        assertThat(serviceAreas.get(2).getName()).isEqualTo("Housing");
        assertThat(serviceAreas.get(3).getName()).isEqualTo("Bankruptcy");
        assertThat(serviceAreas.get(4).getName()).isEqualTo("Benefits");
        assertThat(serviceAreas.get(5).getName()).isEqualTo("Claims against employers");
        assertThat(serviceAreas.get(6).getName()).isEqualTo("Tax");
        assertThat(serviceAreas.get(7).getName()).isEqualTo("Single Justice Procedure");
    }
}
