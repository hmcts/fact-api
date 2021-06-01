package uk.gov.hmcts.dts.fact.util;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;

import java.util.List;

@Component
@SuppressWarnings("PMD.LawOfDemeter")
public class MapCourtCode {

    public Court mapCourtCodesForCourtEntity(final List<CourtType> courtTypes, final Court courtEntity) {
        for (final CourtType c : courtTypes) {
            final uk.gov.hmcts.dts.fact.util.CourtType courtType = uk.gov.hmcts.dts.fact.util.CourtType.findByName(c.getName());
            if (courtType.getCourtCodeConsumer() != null) {
                courtType.setCourtCodeInEntity(courtEntity, c.getCode());
            }
        }
        return courtEntity;
    }

    public List<CourtType> mapCourtCodesForCourtTypeModel(final List<CourtType> courtTypes, final Court court) {
        for (final CourtType c : courtTypes) {
            final uk.gov.hmcts.dts.fact.util.CourtType courtType = uk.gov.hmcts.dts.fact.util.CourtType.findByName(c.getName());
            if (courtType.getCourtCodeFunction() != null) {
                c.setCode(courtType.getCourtCodeFromEntity(court));
            }
        }
        return courtTypes;
    }
}
