package uk.gov.hmcts.dts.fact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.util.FunctionalTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith({SpringExtension.class})
class ServiceAreaEndpointTest extends FunctionalTestBase {

    @Test
    void shouldRetrieveServiceArea() {
        final var response = doGetRequest("/service-areas/money-claims");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceArea serviceArea = response.getBody().as(ServiceArea.class);
        assertThat(serviceArea.getName()).isEqualTo("Money claims");
    }
}
