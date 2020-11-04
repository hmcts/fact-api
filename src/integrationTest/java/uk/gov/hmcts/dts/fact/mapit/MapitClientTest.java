package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MapitClientTest {

    @Autowired
    MapitClient mapitClient;

    @Test
    void should() {
        Coordinates coordinates = mapitClient.getCoordinates("OX1 1RZ");
        @SuppressWarnings("PMD.UseUnderscoresInNumericLiterals")
        Coordinates expected = new Coordinates(51.75023110462087, -1.2673667768810715);
        assertThat(coordinates).isEqualTo(expected);
    }
}
