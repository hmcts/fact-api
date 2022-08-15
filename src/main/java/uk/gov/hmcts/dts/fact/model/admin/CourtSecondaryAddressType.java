package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

/**
 * The admin portal will send across more information about the
 * areas of law and court types, so that we can save the information
 * for the court more effectively.
 */
@Data
@JsonPropertyOrder({"areas_of_law", "courts"})
public class CourtSecondaryAddressType {

    @JsonProperty("areas_of_law")
    private List<AreaOfLaw> areaOfLawList;
    @JsonProperty("courts")
    private List<CourtType> courtTypesList;

    public CourtSecondaryAddressType(List<AreaOfLaw> areasOfLawList,
                                     List<CourtType> courtTypesList) {
        this.areaOfLawList = areasOfLawList;
        this.courtTypesList = courtTypesList;
    }
}
