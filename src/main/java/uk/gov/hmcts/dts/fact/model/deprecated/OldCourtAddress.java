package uk.gov.hmcts.dts.fact.model.deprecated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        this.address = courtAddress.getAddress();
        this.townName = courtAddress.getTownName();
        this.postcode = courtAddress.getPostcode();
    }
}
