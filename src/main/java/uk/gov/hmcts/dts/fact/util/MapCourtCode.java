package uk.gov.hmcts.dts.fact.util;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;

import java.util.List;

@Component
public class MapCourtCode {

    static final int MAGISTRATE_COURT_TYPE_ID = 11_416;
    static final int COUNTY_COURT_TYPE_ID = 11_419;
    static final int CROWN_COURT_TYPE_ID = 11_420;

    public Court mapCourtCodesForCourtEntity(final List<CourtType> courtTypes, final Court courtEntity) {

        //set court codes in Court Entity
        for (final CourtType courtType : courtTypes) {


            switch (courtType.getId()) {
                case MAGISTRATE_COURT_TYPE_ID:
                    courtEntity.setMagistrateCode(courtType.getCode());
                    break;
                case COUNTY_COURT_TYPE_ID:
                    courtEntity.setCciCode(courtType.getCode());
                    break;
                case CROWN_COURT_TYPE_ID:
                    courtEntity.setNumber(courtType.getCode());
                    break;
                default:
                    break;
            }
        }


        return courtEntity;
    }

    public List<CourtType> mapCourtCodesForCourtTypeModel(final List<CourtType> courtTypes, final Court court) {
        for (final CourtType courtType : courtTypes) {

            switch (courtType.getId()) {
                case MAGISTRATE_COURT_TYPE_ID:
                    courtType.setCode(court.getMagistrateCode());
                    break;
                case COUNTY_COURT_TYPE_ID:
                    courtType.setCode(court.getCciCode());
                    break;
                case CROWN_COURT_TYPE_ID:
                    courtType.setCode(court.getNumber());
                    break;
                default:
                    break;
            }
        }

        return courtTypes;

    }

}
