package uk.gov.hmcts.dts.fact.model;

import lombok.Data;

@Data
public class MatrixCell {

    private int statusCode;
    private int originIndex;
    private int destinationIndex;

    private int durationInSeconds;
    private int durationTrafficInSeconds;

    private double distanceInMeters;
}

