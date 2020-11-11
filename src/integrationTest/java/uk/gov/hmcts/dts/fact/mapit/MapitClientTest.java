package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MapitClientTest {

    @Autowired
    private MapitClient mapitClient;

    @Test
    void shouldReturnExpectedCoordinatesForPostcode() {
        final Coordinates coordinates = mapitClient.getCoordinates("OX1 1RZ");
        @SuppressWarnings("PMD.UseUnderscoresInNumericLiterals")
        final Coordinates expected = new Coordinates(51.75023110462087, -1.2673667768810715);
        assertThat(coordinates.isPresent()).isEqualTo(true);
        assertThat(coordinates).isEqualTo(expected);
    }

    @Test
    void shouldReturnBlankCoordinatesForUnsupportedPostcode() {
        final Coordinates coordinates = mapitClient.getCoordinates("JE3 4BA");
        assertThat(coordinates.isPresent()).isEqualTo(false);
    }

    @Test
    void shouldReturnBlankCoordinatesForPartialPostcode() {
        final Coordinates coordinates = mapitClient.getCoordinates("OX1");
        assertThat(coordinates.isPresent()).isEqualTo(false);
    }

    @Test
    void shouldReturnBlankCoordinatesForBlankPostcode() {
        final Coordinates coordinates = mapitClient.getCoordinates("");
        assertThat(coordinates.isPresent()).isEqualTo(false);
    }
}
