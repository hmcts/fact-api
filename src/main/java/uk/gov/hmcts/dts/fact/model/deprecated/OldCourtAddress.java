package uk.gov.hmcts.dts.fact.model.deprecated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"type", "address", "town", "postcode"})
public class OldCourtAddress {

    @JsonProperty("type")
    private String addressType;
    private String address;
    @JsonProperty("town")
    private String townName;
    private String postcode;

    public OldCourtAddress(uk.gov.hmcts.dts.fact.entity.CourtAddress courtAddress) {
        this.addressType = courtAddress.getAddressType().getName();
        this.address = chooseString(courtAddress.getAddressCy(), courtAddress.getAddress());
        this.townName = chooseString(courtAddress.getTownNameCy(), courtAddress.getTownName());
        this.postcode = courtAddress.getPostcode();
    }
}
