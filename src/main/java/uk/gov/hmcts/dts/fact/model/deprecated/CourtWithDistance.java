package uk.gov.hmcts.dts.fact.model.deprecated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.*;
import uk.gov.hmcts.dts.fact.model.AreaOfLaw;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@SuppressWarnings("PMD.TooManyFields")
@JsonPropertyOrder({"name", "lat", "lon", "number", "cci_code", "magistrate_code", "slug", "types", "address",
    "areas_of_law", "displayed", "hide_aols", "dx_number", "distance"})
public class CourtWithDistance {
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
    private uk.gov.hmcts.dts.fact.model.CourtAddress address;
    private List<AreaOfLaw> areasOfLaw;
    private Boolean displayed;
    private Boolean hideAols;
    private String dxNumber;
    private BigDecimal distance;


    public CourtWithDistance(final Court courtEntity) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.lat = courtEntity.getLat();
        this.lon = courtEntity.getLon();
        this.crownLocationCode = courtEntity.getNumber();
        this.countyLocationCode = courtEntity.getCciCode();
        this.magistratesLocationCode = courtEntity.getMagistrateCode();
        this.slug = courtEntity.getSlug();
        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).sorted().collect(toList());
        this.address = mapAddress(courtEntity.getAddresses());
        this.areasOfLaw = courtEntity.getAreasOfLaw().stream().map(AreaOfLaw::new).collect(toList());
        this.displayed = courtEntity.getDisplayed();
        this.hideAols = courtEntity.getHideAols();
        this.dxNumber = getDxNumber(courtEntity);
    }

    public CourtWithDistance(final uk.gov.hmcts.dts.fact.entity.CourtWithDistance courtWithDistanceEntity) {
        this.name = chooseString(courtWithDistanceEntity.getNameCy(), courtWithDistanceEntity.getName());
        this.lat = courtWithDistanceEntity.getLat();
        this.lon = courtWithDistanceEntity.getLon();
        this.crownLocationCode = courtWithDistanceEntity.getNumber();
        this.countyLocationCode = courtWithDistanceEntity.getCciCode();
        this.magistratesLocationCode = courtWithDistanceEntity.getMagistrateCode();
        this.slug = courtWithDistanceEntity.getSlug();
        this.courtTypes = courtWithDistanceEntity.getCourtTypes().stream().map(CourtType::getName).sorted().collect(toList());
        this.address = mapAddress(courtWithDistanceEntity.getAddresses());
        this.areasOfLaw = courtWithDistanceEntity.getAreasOfLaw().stream().map(AreaOfLaw::new).collect(toList());
        this.displayed = courtWithDistanceEntity.getDisplayed();
        this.hideAols = courtWithDistanceEntity.getHideAols();
        this.dxNumber = getDxNumber(courtWithDistanceEntity);
        this.distance = BigDecimal.valueOf(courtWithDistanceEntity.getDistance()).setScale(2, RoundingMode.HALF_UP);
    }

    private String getDxNumber(final Court courtEntity) {
        return ofNullable(courtEntity.getCourtDxCodes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(c -> c.getDxCode().getCode())
            .findFirst()
            .orElse(null);
    }

    private String getDxNumber(final uk.gov.hmcts.dts.fact.entity.CourtWithDistance courtWithDistanceEntity) {
        return ofNullable(courtWithDistanceEntity.getDxCodes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(DxCode::getCode)
            .findFirst()
            .orElse(null);
    }

    private uk.gov.hmcts.dts.fact.model.CourtAddress mapAddress(List<CourtAddress> courtAddresses) {
        return courtAddresses
            .stream()
            .filter(a -> "Postal".equals(a.getAddressType().getName()))
            .findFirst()
            .map(uk.gov.hmcts.dts.fact.model.CourtAddress::new)
            .orElse(
                courtAddresses
                    .stream()
                    .findFirst()
                    .map(uk.gov.hmcts.dts.fact.model.CourtAddress::new)
                    .orElse(null)
            );
    }
}
