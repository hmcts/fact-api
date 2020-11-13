package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapitDataTest {

    @Test
    void shouldReturnTrueIfLonAndLatPresent() {
        final MapitData mapitData = new MapitData(51.7, -1.2, null, null);
        assertThat(mapitData.hasLatAndLonValues()).isTrue();
    }

    @Test
    void shouldReturnFalseIfLonNotPresent() {
        final MapitData mapitData = new MapitData(51.7, null, null, null);
        assertThat(mapitData.hasLatAndLonValues()).isFalse();
    }

    @Test
    void shouldReturnFalseIfLatNotPresent() {
        final MapitData mapitData = new MapitData(null, -1.2, null, null);
        assertThat(mapitData.hasLatAndLonValues()).isFalse();
    }

    @Test
    void shouldReturnFalseIfLatAndLonNotPresent() {
        final MapitData mapitData = new MapitData(null, null, null, null);
        assertThat(mapitData.hasLatAndLonValues()).isFalse();
    }
}
