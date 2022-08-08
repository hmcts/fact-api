package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.model.CourtSecondaryAddressType;

import java.util.List;

import static uk.gov.hmcts.dts.fact.util.Utils.constructAddressLines;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CourtAddress {
    private Integer id;
    @JsonProperty("type_id")
    private Integer addressTypeId;
    @JsonProperty("address_lines")
    private List<String> addressLines;
    @JsonProperty("address_lines_cy")
    private List<String> addressLinesCy;
    @JsonProperty("town")
    private String townName;
    @JsonProperty("town_cy")
    private String townNameCy;
    @JsonProperty("county_id")
    private Integer countyId;
    private String postcode;
    @JsonProperty("types")
    private CourtSecondaryAddressType courtSecondaryAddressType;

    public CourtAddress(final uk.gov.hmcts.dts.fact.entity.CourtAddress courtAddress) {
        this.id = courtAddress.getId();
        if (courtAddress.getAddressType() != null) {
            this.addressTypeId = courtAddress.getAddressType().getId();
        }
        this.addressLines = constructAddressLines(courtAddress.getAddress());
        this.addressLinesCy = constructAddressLines(courtAddress.getAddressCy());
        this.townName = courtAddress.getTownName();
        this.townNameCy = courtAddress.getTownNameCy();
        if (courtAddress.getCounty() != null) {
            this.countyId = courtAddress.getCounty().getId();
        }
        this.postcode = courtAddress.getPostcode();
    }
}
