package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MapitData {
    private static final String COUNCIL = "council";
    private static final String COUNTY = "county";

    @JsonProperty("wgs84_lat")
    Double lat;
    @JsonProperty("wgs84_lon")
    Double lon;

    JsonNode shortcuts;

    JsonNode areas;

    public boolean hasLatAndLonValues() {
        return null != getLat() && null != getLon();
    }

    public Optional<String> getLocalAuthority() {
        return getCouncilNumberFromObject()
            .flatMap(this::getCouncilNameFromAreas);
    }

    private Optional<String> getCouncilNumberFromObject() {
        return ofNullable(shortcuts)
            .map(s -> s.get(COUNCIL))
            .map(c -> c.get(COUNTY))
            .map(JsonNode::asText)
            .or(this::getCouncilNumberFromValue);
    }

    private Optional<String> getCouncilNumberFromValue() {
        return ofNullable(shortcuts)
            .map(s -> s.get(COUNCIL))
            .map(JsonNode::asText);
    }

    private Optional<String> getCouncilNameFromAreas(String area) {
        return ofNullable(areas)
            .map(string -> areas.get(area))
            .map(a -> a.get("name"))
            .map(JsonNode::asText);
    }

    public String getRegionFromMapitData() {
        // We will only ever have one ER, or one WAE
        // For english regions
        for (JsonNode mapitArea : this.areas) {
            System.out.println(mapitArea.get("type").asText());
            if (mapitArea.get("type").asText().equals("ER"))
                return mapitArea.get("name").asText();
        }

        // For welsh regions
        for (JsonNode mapitArea : this.areas) {
            System.out.println(mapitArea.get("type").asText());
            if (mapitArea.get("type").asText().equals("WAE"))
                return mapitArea.get("name").asText();
        }

        // If we have no region at all
        throw new NotFoundException("Could not find region for query");
    }
}
