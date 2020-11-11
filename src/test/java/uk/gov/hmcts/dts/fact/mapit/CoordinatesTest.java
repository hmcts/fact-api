package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinatesTest {

    @Test
    void shouldReturnTrueIfLonAndLatPresent() {
        final Coordinates coordinates = new Coordinates(51.7, -1.2);
        assertThat(coordinates.isPresent()).isTrue();
    }

    @Test
    void shouldReturnFalseIfLonNotPresent() {
        final Coordinates coordinates = new Coordinates(51.7, null);
        assertThat(coordinates.isPresent()).isFalse();
    }

    @Test
    void shouldReturnFalseIfLatNotPresent() {
        final Coordinates coordinates = new Coordinates(null, -1.2);
        assertThat(coordinates.isPresent()).isFalse();
    }

    @Test
    void shouldReturnFalseIfLatAndLonNotPresent() {
        final Coordinates coordinates = new Coordinates(null, null);
        assertThat(coordinates.isPresent()).isFalse();
    }
}
