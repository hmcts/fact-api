package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"address_lines", "postcode", "town", "type"})
public class CourtAddress {

    @JsonProperty("type")
    private String addressType;
    @JsonProperty("address_lines")
    private List<String> addressLines;
    @JsonProperty("town")
    private String townName;
    private String postcode;

    public CourtAddress(uk.gov.hmcts.dts.fact.entity.CourtAddress courtAddress) {
        this.addressType = courtAddress.getAddressType().getName();
        this.addressLines = this.getLines(courtAddress);
        this.townName = courtAddress.getTownName();
        this.postcode = courtAddress.getPostcode();
    }

    private List<String> getLines(uk.gov.hmcts.dts.fact.entity.CourtAddress courtAddress) {
        return courtAddress
            .getAddress()
            .lines()
            .filter(not(String::isEmpty))
            .collect(toList());
    }
}
