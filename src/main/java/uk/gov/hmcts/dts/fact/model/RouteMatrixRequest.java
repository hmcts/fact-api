package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteMatrixRequest {

    private final String type = "FeatureCollection";

    private List<Feature> features;

    private String travelMode = "driving";
    private String optimizeRoute = "fastest";
}

