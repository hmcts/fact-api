package uk.gov.hmcts.dts.fact.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feature {

    private final String type = "Feature";

    private Geometry geometry;

    private Map<String, String> properties;
}

