package uk.gov.hmcts.dts.fact.services.search;

import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;

import java.util.List;

public interface Search {

    List<CourtReferenceWithDistance> search(final MapitData mapitData, final String postcode, final String areaOfLaw);
}
