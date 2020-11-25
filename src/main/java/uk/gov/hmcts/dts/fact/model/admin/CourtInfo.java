package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.Court;

import static uk.gov.hmcts.dts.fact.util.Utils.stripHtmlFromString;

@NoArgsConstructor

@Getter
@Setter
public class CourtInfo {
    protected String info;
    @JsonProperty("info_cy")
    protected String infoCy;

    public CourtInfo(String info, String infoCy) {
        this.info = info;
        this.infoCy = infoCy;
    }

    public CourtInfo(Court court) {
        this.info = stripHtmlFromString(court.getInfo());
        this.infoCy = stripHtmlFromString(court.getInfoCy());
    }

}
