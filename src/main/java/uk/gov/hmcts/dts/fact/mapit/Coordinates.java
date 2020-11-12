package uk.gov.hmcts.dts.fact.mapit;

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

    public boolean hasLatAndLonValues() {
        return null != getLat() && null != getLon();
    }
}
