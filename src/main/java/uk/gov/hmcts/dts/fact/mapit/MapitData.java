package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public String getMatchingRegionNumber(Map<String, MapitArea> regions) {
//        System.out.println("OUTPUT FROM MATCHING METHOD: " +
//            ofNullable(areas)
//            .filter(area -> area.equals(REGION_IDS))
//            .map(string -> areas.get(String.valueOf(REGION_IDS)))
//        );

        //System.out.println("REGIONS: " + regions);
        //System.out.println("AREAS: " + areas);
        System.out.println("Regions KEYS: " + regions.keySet());
        //System.out.println("What does this do?.. " + ofNullable(areas).filter(a -> a.);
        System.out.println("Areas ELEMENTS:\n");

        areas.forEach(a -> System.out.println(a.get("id")));


/*        Optional<String> success = null;
        int index = -1;
        for (String id: REGION_IDS) {
            //System.out.println(ofNullable(areas).map(string -> areas.get(id)));
            Optional<JsonNode> match = ofNullable(areas).map(string -> areas.get(id));
            if (!match.isEmpty()) {
                //System.out.println(match.map(m -> m.get("id")).map(JsonNode::asText));
                success = match.map(m -> m.get("id")).map(JsonNode::asText);
            }
            index++;
        }
        System.out.println(REGION_IDS.get(index));*/
        //return REGION_IDS.get(index);
        //System.out.println("OUTPUT OF CONTAINS: " + REGION_IDS.contains(ofNullable(areas)));

        return "164858";
    }

    public Optional<String> getMatchingRegionNameFromAreas(String area) {
        return ofNullable(areas)
            .map(string -> areas.get(area))
            .map(a -> a.get("name"))
            .map(JsonNode::asText);
    }
}
