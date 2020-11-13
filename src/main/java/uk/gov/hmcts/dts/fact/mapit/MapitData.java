package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapitData {
    @JsonProperty("wgs84_lat")
    Double lat;
    @JsonProperty("wgs84_lon")
    Double lon;

    JsonNode shortcuts;

    JsonNode areas;

    public boolean hasLatAndLonValues() {
        return null != getLat() && null != getLon();
    }

    Optional<String> getLocalAuthority() {

        return getCouncilNumberFromObject()
            .map(string -> areas.get(string))
            .map(area -> area.get("name"))
            .map(JsonNode::asText);
    }


    private Optional<String> getCouncilNumberFromObject() {
        return ofNullable(shortcuts)
            .map(s -> s.get("council"))
            .map(c -> c.get("county"))
            .map(JsonNode::asText)
            .or(this::getCouncilNumberFromValue);
    }


    private Optional<String> getCouncilNumberFromValue() {
        return ofNullable(shortcuts)
            .map(s -> s.get("council"))
            .map(JsonNode::asText);
    }
}
