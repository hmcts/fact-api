package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"areas_of_law", "courts"})
public class CourtSecondaryAddressType {

    @JsonProperty("areas_of_law")
    private List<String> areaOfLawList;
    @JsonProperty("courts")
    private List<String> courtTypesList;

    public CourtSecondaryAddressType(List<String> areasOfLawList,
                                      List<String> courtTypesList) {
        this.areaOfLawList = areasOfLawList;
        this.courtTypesList = courtTypesList;
    }
}
