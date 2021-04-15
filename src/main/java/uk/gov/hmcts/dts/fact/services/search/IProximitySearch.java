package uk.gov.hmcts.dts.fact.services.search;

import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import java.util.List;

public interface IProximitySearch {

    List<CourtWithDistance> searchWith(final MapitData mapitData);
}
