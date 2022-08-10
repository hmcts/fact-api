package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;
import static uk.gov.hmcts.dts.fact.util.Utils.constructAddressLines;

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
    private String county;
    private String postcode;
    private String description;
    @JsonProperty("types")
    private CourtSecondaryAddressType courtSecondaryAddressType;

    public CourtAddress(uk.gov.hmcts.dts.fact.entity.CourtAddress courtAddress) {
        this.addressType = chooseString(
            courtAddress.getAddressType().getNameCy(),
            courtAddress.getAddressType().getName()
        );
        this.addressLines = constructAddressLines(chooseString(courtAddress.getAddressCy(), courtAddress.getAddress()));
        this.townName = chooseString(courtAddress.getTownNameCy(), courtAddress.getTownName());
        this.county = courtAddress.getCounty() == null ? "" : courtAddress.getCounty().getName();
        this.postcode = courtAddress.getPostcode();

        if (courtAddress.getCourtSecondaryAddressType().size() > 0) {
            this.courtSecondaryAddressType = new CourtSecondaryAddressType(
                courtAddress
                    .getCourtSecondaryAddressType()
                    .stream()
                    .filter(a -> Objects.nonNull(a.getAreaOfLaw()))
                    .map(s -> s.getAreaOfLaw().getName())
                    .collect(Collectors.toList()),
                courtAddress
                    .getCourtSecondaryAddressType()
                    .stream()
                    .filter(a -> Objects.nonNull(a.getCourtType()))
                    .map(s -> s.getCourtType().getName())
                    .collect(Collectors.toList())
            );
        }
    }
}
