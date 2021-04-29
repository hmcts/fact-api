package uk.gov.hmcts.dts.fact.util;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;

import java.util.List;

@Component
public class MapCourtCode {

    public Court mapCourtCodesForCourtEntity(final List<uk.gov.hmcts.dts.fact.model.admin.CourtType> courtTypes, final Court courtEntity) {

        //set court codes in Court Entity
        for (final uk.gov.hmcts.dts.fact.model.admin.CourtType courtType : courtTypes) {

            switch (courtType.getName()) {
                case "Magistrates' Court":
                    courtEntity.setMagistrateCode(courtType.getCode());
                    break;
                case "County Court":
                    courtEntity.setCciCode(courtType.getCode());
                    break;
                case "Crown Court":
                    courtEntity.setNumber(courtType.getCode());
                    break;
                default:
                    break;
            }
        }

        return courtEntity;
    }

    public List<uk.gov.hmcts.dts.fact.model.admin.CourtType> mapCourtCodesForCourtTypeModel(final List<uk.gov.hmcts.dts.fact.model.admin.CourtType> courtTypes, final Court court) {
        for (final CourtType courtType : courtTypes) {

            switch (courtType.getName()) {
                case "Magistrates' Court":
                    courtType.setCode(court.getMagistrateCode());
                    break;
                case "County Court":
                    courtType.setCode(court.getCciCode());
                    break;
                case "Crown Court":
                    courtType.setCode(court.getNumber());
                    break;
                default:
                    break;
            }
        }

        return courtTypes;

    }

}
