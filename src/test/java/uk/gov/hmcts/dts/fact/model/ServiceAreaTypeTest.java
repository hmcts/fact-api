package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.CIVIL;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.FAMILY;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.OTHER;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.serviceAreaTypeFrom;

class ServiceAreaTypeTest {

    @Test
    void shouldConvertTypeStringToServiceAreaType() {
        assertThat(serviceAreaTypeFrom("family")).isEqualTo(FAMILY);
        assertThat(serviceAreaTypeFrom("civil")).isEqualTo(CIVIL);
        assertThat(serviceAreaTypeFrom("other")).isEqualTo(OTHER);
        assertThat(serviceAreaTypeFrom("something-else")).isEqualTo(OTHER);
    }
}
