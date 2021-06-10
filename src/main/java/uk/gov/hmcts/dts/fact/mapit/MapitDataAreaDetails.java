package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapitDataAreaDetails {

    private String id;
    private String parentArea;
    private int generationHigh;
    private String name;
    private String country;
    private String typeName;
    private String generationLow;
    private String countryName;
    private String type;
}
