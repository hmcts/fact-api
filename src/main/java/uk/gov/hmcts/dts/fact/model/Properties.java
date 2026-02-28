package uk.gov.hmcts.dts.fact.model;

import lombok.Data;

import java.util.List;

@Data
public class Properties {

    private Summary summary;
    private List<MatrixCell> matrix;
}

