package uk.gov.hmcts.dts.fact.mapit;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class MapitClientTest {

    @Autowired
    private MapitClient mapitClient;

    @Test
    void shouldReturnExpectedCoordinatesForPostcode() {
        final Coordinates coordinates = mapitClient.getCoordinates("OX1 1RZ");
        @SuppressWarnings("PMD.UseUnderscoresInNumericLiterals")
        final Coordinates expected = new Coordinates(51.75023110462087, -1.2673667768810715);
        assertThat(coordinates.hasLatAndLonValues()).isEqualTo(true);
        assertThat(coordinates).isEqualTo(expected);
    }

    @Test
    void shouldReturnBlankCoordinatesForUnsupportedPostcode() {
        final Coordinates coordinates = mapitClient.getCoordinates("JE3 4BA");
        assertThat(coordinates.hasLatAndLonValues()).isEqualTo(false);
    }

    @Test
    void shouldThrowFeignExceptionForPartialPostcode() {
        try {
            mapitClient.getCoordinates("OX1");
            fail();
        } catch (final FeignException ex) {
            assertThat(ex.status()).isEqualTo(400);
        }
    }
}
