package uk.gov.hmcts.dts.fact.services.search;

import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import java.util.List;

@SuppressWarnings("PMD.FinalParameterInAbstractMethod")
public interface Search {

    List<CourtWithDistance> searchWith(
        final ServiceArea serviceArea,
        final MapitData mapitData,
        final String postcode,
        final Boolean includeClosed
        );
}
