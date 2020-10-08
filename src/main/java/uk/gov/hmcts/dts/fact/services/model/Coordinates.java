package uk.gov.hmcts.dts.fact.services.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {
    @JsonProperty("wgs84_lat")
    Double lat;
    @JsonProperty("wgs84_lon")
    Double lon;
}
