package uk.gov.hmcts.dts.fact.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Geometry {

    private final String type = "MultiPoint";

    private List<List<Double>> coordinates;
}

