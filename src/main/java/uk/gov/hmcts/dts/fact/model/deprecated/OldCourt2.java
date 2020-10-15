package uk.gov.hmcts.dts.fact.model.deprecated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.model.AreaOfLaw;
import uk.gov.hmcts.dts.fact.model.CourtAddress;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
@JsonPropertyOrder({"name", "lat", "lon", "number", "cci_code", "magistrate_code", "slug", "types", "address",
    "areas_of_law", "displayed", "hide_aols", "dx_number", "distance" })
public class OldCourt2 {
    private String name;
    private Double lat;
    private Double lon;
    @JsonProperty("number")
    private Integer crownLocationCode;
    @JsonProperty("cci_code")
    private Integer countyLocationCode;
    @JsonProperty("magistrate_code")
    private Integer magistratesLocationCode;
    private String slug;
    @JsonProperty("types")
    private List<String> courtTypes;
    private CourtAddress address;
    @JsonProperty("areas_of_law")
    private List<AreaOfLaw> areasOfLaw;
    private Boolean displayed;
    @JsonProperty("hide_aols")
    private Boolean hideAols;
    @JsonProperty("dx_number")
    private String dxNumber;
    private BigDecimal distance;


    public OldCourt2(uk.gov.hmcts.dts.fact.entity.Court2 courtEntity) {
        this.name = courtEntity.getName();
        this.lat = courtEntity.getLat();
        this.lon = courtEntity.getLon();
        this.crownLocationCode = courtEntity.getNumber();
        this.countyLocationCode = courtEntity.getCciCode();
        this.magistratesLocationCode = courtEntity.getMagistrateCode();
        this.slug = courtEntity.getSlug();
        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).sorted().collect(toList());
        this.address = this.mapAddress(courtEntity);
        this.areasOfLaw = courtEntity.getAreasOfLaw().stream().map(AreaOfLaw::new).collect(toList());
        this.displayed = courtEntity.getDisplayed();
        this.hideAols = courtEntity.getHideAols();
        this.dxNumber = this.getDxNumber(courtEntity);
        this.distance = BigDecimal.valueOf(courtEntity.getDistance()).setScale(2, RoundingMode.HALF_UP);
    }

    private String getDxNumber(uk.gov.hmcts.dts.fact.entity.Court2 courtEntity) {
        return courtEntity
            .getContacts()
            .stream()
            .filter(c -> "DX".equals(c.getName()))
            .map(Contact::getNumber)
            .findFirst()
            .orElse(null);
    }

    private CourtAddress mapAddress(uk.gov.hmcts.dts.fact.entity.Court2 courtEntity) {
        return courtEntity
            .getAddresses()
            .stream()
            .filter(a -> "Postal".equals(a.getAddressType().getName()))
            .findFirst()
            .map(CourtAddress::new)
            .orElse(
                courtEntity
                    .getAddresses()
                    .stream()
                    .findFirst()
                    .map(CourtAddress::new)
                    .orElse(null)
            );
    }
}
