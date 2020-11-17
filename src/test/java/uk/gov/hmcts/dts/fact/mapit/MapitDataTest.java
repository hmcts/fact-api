package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MapitDataTest {

    private static final String COUNCIL = "council";
    private static final String COUNTY = "county";
    private static final String COUNCIL_AREA_NUMBER = "12345";
    private static final String COUNCIL_12345 = "Council 12345";

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


    @Test
    void shouldReturnOptionalEmptyWhenShortCutsNotFound() {
        final MapitData mapitData = new MapitData(null, null, null, null);
        assertThat(mapitData.getLocalAuthority()).isEmpty();
    }

    @Test
    void shouldReturnOptionalEmptyWhenCouncilShortcutCantBeFound() {
        final JsonNode shortcuts = mock(JsonNode.class);
        when(shortcuts.get(matches(COUNCIL))).thenReturn(null);
        final MapitData mapitData = new MapitData(null, null, shortcuts, null);
        assertThat(mapitData.getLocalAuthority()).isEmpty();
    }

    @Test
    void shouldReturnOptionalEmptyWhenAreasCantBeFound() {
        final JsonNode shortcuts = mock(JsonNode.class);
        when(shortcuts.get(matches(COUNCIL))).thenReturn(shortcuts);
        when(shortcuts.get(matches(COUNTY))).thenReturn(shortcuts);
        when(shortcuts.asText()).thenReturn(COUNCIL_AREA_NUMBER);
        final MapitData mapitData = new MapitData(null, null, shortcuts, null);
        assertThat(mapitData.getLocalAuthority()).isEmpty();
    }

    @Test
    void shouldReturnOptionalEmptyWhenAreaCantBeFound() {
        final JsonNode shortcuts = mock(JsonNode.class);
        final JsonNode areas = mock(JsonNode.class);
        when(shortcuts.get(matches(COUNCIL))).thenReturn(shortcuts);
        when(shortcuts.get(matches(COUNTY))).thenReturn(shortcuts);
        when(shortcuts.asText()).thenReturn(COUNCIL_AREA_NUMBER);
        when(areas.get(matches(COUNCIL_AREA_NUMBER))).thenReturn(null);
        final MapitData mapitData = new MapitData(null, null, shortcuts, areas);
        assertThat(mapitData.getLocalAuthority()).isEmpty();
    }

    @Test
    void shouldReturnLocalAuthorityNameFromObject() {
        final JsonNode shortcuts = mock(JsonNode.class);
        when(shortcuts.get(matches(COUNCIL))).thenReturn(shortcuts);
        when(shortcuts.get(matches(COUNTY))).thenReturn(shortcuts);
        when(shortcuts.asText()).thenReturn(COUNCIL_AREA_NUMBER);
        final JsonNode areas = mock(JsonNode.class);
        final JsonNode result = mock(JsonNode.class);
        when(result.asText()).thenReturn(COUNCIL_12345);
        when(areas.get(matches(COUNCIL_AREA_NUMBER))).thenReturn(areas);
        when(areas.get(matches("name"))).thenReturn(result);
        final MapitData mapitData = new MapitData(null, null, shortcuts, areas);
        assertThat(mapitData.getLocalAuthority()).isPresent();
        assertThat(mapitData.getLocalAuthority()).contains(COUNCIL_12345);
    }

    @Test
    void shouldReturnLocalAuthorityNameFormValue() {
        final JsonNode shortcuts = mock(JsonNode.class);
        when(shortcuts.get(matches(COUNCIL))).thenReturn(shortcuts);
        when(shortcuts.get(matches(COUNTY))).thenReturn(null);
        when(shortcuts.asText()).thenReturn(COUNCIL_AREA_NUMBER);

        final JsonNode areas = mock(JsonNode.class);
        final JsonNode result = mock(JsonNode.class);
        when(result.asText()).thenReturn(COUNCIL_12345);
        when(areas.get(matches(COUNCIL_AREA_NUMBER))).thenReturn(areas);
        when(areas.get(matches("name"))).thenReturn(result);
        final MapitData mapitData = new MapitData(null, null, shortcuts, areas);
        assertThat(mapitData.getLocalAuthority()).isPresent();
        assertThat(mapitData.getLocalAuthority()).contains(COUNCIL_12345);
    }
}
