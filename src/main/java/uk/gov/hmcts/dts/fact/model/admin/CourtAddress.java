package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static uk.gov.hmcts.dts.fact.util.Utils.constructAddressLines;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuppressWarnings("PMD.NullAssignment")
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
    @JsonProperty("fields_of_law")
    private CourtSecondaryAddressType courtSecondaryAddressType;
    @JsonProperty("sort_order")
    private Integer sortOrder;
    @JsonProperty("epim_id")
    private String epimId;

    public CourtAddress(final uk.gov.hmcts.dts.fact.entity.CourtAddress courtAddress) {
        this.id = Objects.isNull(courtAddress.getId()) ? null : courtAddress.getId();
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
        this.courtSecondaryAddressType = Objects.isNull(courtAddress.getCourtSecondaryAddressType()) ? new CourtSecondaryAddressType(
            Collections.emptyList(), Collections.emptyList()) :
            new CourtSecondaryAddressType(
                courtAddress
                    .getCourtSecondaryAddressType()
                    .stream()
                    .filter(a -> Objects.nonNull(a.getAreaOfLaw()))
                    .map(a -> new AreaOfLaw(a.getAreaOfLaw()))
                    .collect(Collectors.toList()),
                courtAddress
                    .getCourtSecondaryAddressType()
                    .stream()
                    .filter(a -> Objects.nonNull(a.getCourtType()))
                    .map(s -> new CourtType(s.getCourtType()))
                    .collect(Collectors.toList())
            );
        this.sortOrder = courtAddress.getSortOrder();
        this.epimId = courtAddress.getEpimId();
    }
}
