package uk.gov.hmcts.dts.fact.model;

import lombok.Data;

@Data
public class RouteMatrixResponse {

    private String type;
    private Geometry geometry;
    private Properties properties;
}

