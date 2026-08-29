package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourtByRoadDistance {
    private Integer id;
    private String name;
    private String slug;
    private Double lat;
    private Double lon;
    private Double distanceMiles;
    private Double travelTimeMinutes;
}
